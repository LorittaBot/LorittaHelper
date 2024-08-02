package net.perfectdreams.loritta.helper.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import dev.minn.jda.ktx.messages.MessageCreate
import io.ktor.http.*
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.tables.SelectedResponsesLog
import net.perfectdreams.loritta.helper.tables.StartedSupportSolicitations
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.generateserverreport.EncryptionUtils
import net.perfectdreams.loritta.helper.utils.tickets.FakePrivateThreadChannel
import net.perfectdreams.loritta.helper.utils.tickets.TicketSystemTypeData
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.helper.utils.tickets.TicketsCache
import net.perfectdreams.loritta.helper.utils.tickets.systems.HelpDeskTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.SparklyPowerHelpDeskTicketSystem
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.concurrent.TimeUnit

class ComponentInteractionListener(val m: LorittaHelper) : ListenerAdapter() {
    companion object {
        const val MY_QUESTION_ISNT_HERE_SPECIAL_KEY = "MyQuestionIsntHere!"
        private val logger = KotlinLogging.logger {}
    }

    val recentlyCreatedTickets = Caffeine.newBuilder()
        .expireAfterWrite(5L, TimeUnit.MINUTES)
        .build<Long, Long>()
        .asMap()

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        logger.info { "Button Interaction ${event.user.idLong} - ${event.channel.idLong}: ${event.componentId}" }

        val (id, data) = event.componentId.split(":")

        when (id) {
            "create_ticket" -> {
                m.launch {
                    createTicket(event, data)
                }
            }
            "close_ticket" -> {
                closeTicket(event, data)
            }
            "open_report_form" -> {
                openReportForm(event)
            }
        }
    }

    private fun openReportForm(event: ButtonInteractionEvent) {
        val json = buildJsonObject {
            put("user", event.user.idLong)
            put("time", System.currentTimeMillis())
        }

        val encryptedInformation = EncryptionUtils.encryptMessage(m.helperConfig.secretKey, json.toString())

        event.interaction.reply(
            """**Então... você está afim de denunciar uns meliantes? Então você veio ao lugar certo! <:lorota_jubinha:500766283965661184>**
                        |
                        |Para fazer a sua denúncia, acesse o nosso formulário para preencher e enviar a sua denúncia!
                        |
                        |**Ao abrir o formulário, o código de acesso será preenchido automaticamente mas, caso não seja preenchido, copie o código e coloque no formulário!** `$encryptedInformation`
                        |
                        |*Observação: Não envie o link do formulário e nem o seu código para outras pessoas! Esse formulário é único e especial apenas para você e, se você passar para outras pessoas, elas vão poder fazer denúncias com o seu nome! Se você queria denunciar alguém de novo, clique no botão novamente!*
                        |
                        |https://docs.google.com/forms/d/e/1FAIpQLSe6NBwXkl2ZY9MpSfFcTO6gXEtDTTQSTX2pQouzamWV_5h5zw/viewform?usp=pp_url&entry.645865978=${encryptedInformation.encodeURLParameter()}
                    """.trimMargin()
        ).setEphemeral(true).queue()
    }

    private suspend fun createTicket(event: ButtonInteractionEvent, data: String) {
        try {
            val member = event.member!!

            // Check if user is banned from Loritta, because it is super annoying them creating tickets just to ask them to be unbanned
            val currentBanState = transaction(m.databases.lorittaDatabase) {
                BannedUsers.select {
                    BannedUsers.userId eq event.user.idLong and
                            (BannedUsers.valid eq true) and
                            (
                                    BannedUsers.expiresAt.isNull()
                                            or
                                            (BannedUsers.expiresAt.isNotNull() and (BannedUsers.expiresAt greaterEq System.currentTimeMillis())))
                }
                    .orderBy(BannedUsers.bannedAt, SortOrder.DESC)
                    .limit(1)
                    .firstOrNull()
            }

            // If the user is banned, just ignore
            if (currentBanState != null)
                return

            val ticketSystemTypeData = ComponentDataUtils.decode<TicketSystemTypeData>(data)
            val systemInfo = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType)
            val language = systemInfo.getI18nContext(m.languageManager)

            val hook = event.interaction.deferReply(true).await()

            if (systemInfo.systemType == TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE && member.roles.any { it.idLong == 341343754336337921L }) { // Desenhistas role
                hook.editOriginal("Você já tem o cargo de desenhistas, você não precisa enviar uma \"Primeira Fan Art\" novamente! Caso queira enviar mais fan arts para a galeria, basta enviar em <#583406099047252044>")
                    .await()
                return
            }

            // Avoid users closing and reopening threads constantly
            val lastTicketCreatedAt = recentlyCreatedTickets[event.user.idLong]

            if (lastTicketCreatedAt != null) {
                hook.editOriginal(
                    language.get(
                        I18nKeysData.Tickets.YouAlreadyCreatedATicketRecently(
                            "<:lori_sob:556524143281963008>",
                            "<t:${(lastTicketCreatedAt / 1000) + 300}:R>"
                        )
                    )
                ).await()
                return
            }

            recentlyCreatedTickets[event.user.idLong] = System.currentTimeMillis()

            hook.editOriginal(language.get(I18nKeysData.Tickets.CreatingATicket)).await()

            val cachedTickets = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType).cache
            val alreadyCreatedUserTicketData = cachedTickets.mutex.withLock { cachedTickets.tickets[event.user.idLong] }
            var ticketThreadId = alreadyCreatedUserTicketData?.id

            // Max username size = 32
            // Max ID length (well it can be bigger): 18
            // So if we do the sum of everything...
            // 3 (beginning) + 32 (username) + 2 (space and "(") + 18 (user ID) + 1 (")")
            // = 56
            // Threads can have at most 100 chars!
            val threadName = "\uD83D\uDCE8 ${event.user.name} (${event.user.idLong})"

            if (alreadyCreatedUserTicketData == null) {
                // If it is STILL null, we will create a thread!
                ticketThreadId = event.channel.asThreadContainer()
                    .createThreadChannel(threadName, true)
                    .setAutoArchiveDuration(systemInfo.archiveDuration)
                    .setInvitable(false)
                    .reason("Ticket created for ${event.user.idLong}")
                    .await()
                    .idLong
            }

            ticketThreadId!!

            val threadChannel = event.channel.asThreadContainer().threadChannels
                .firstOrNull { it.idLong == ticketThreadId }
                ?: FakePrivateThreadChannel(
                    ticketThreadId,
                    event.guild!!
                ).setParentChannel(event.channel.asThreadContainer())

            // Update thread metadata and name juuuust to be sure
            threadChannel
                .manager
                .setName(threadName)
                .setArchived(false)
                .setLocked(false)
                .setInvitable(false)
                .reason("Unarchival request via button by ${event.user.name} (${event.user.id})")
                .await()

            // We need to add the user to the thread after it is unarchived!
            threadChannel.addThreadMember(event.user).await()

            cachedTickets.tickets[event.user.idLong] = TicketsCache.DiscordThreadTicketData(ticketThreadId)

            transaction(m.databases.helperDatabase) {
                StartedSupportSolicitations.insert {
                    it[StartedSupportSolicitations.userId] = event.user.idLong
                    it[StartedSupportSolicitations.startedAt] = Instant.now()
                    it[StartedSupportSolicitations.threadId] = ticketThreadId
                    it[StartedSupportSolicitations.systemType] = ticketSystemTypeData.systemType
                }
            }

            threadChannel.sendMessage(
                MessageCreate {
                    systemInfo.ticketCreatedMessage.invoke(this, event.user, language)
                }
            ).await()

            hook.editOriginal(
                language.get(
                    I18nKeysData.Tickets.TicketWasCreated("<#${ticketThreadId}>")
                )
            ).await()
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while trying to create a ticket!" }
        }
    }

    private fun closeTicket(event: ButtonInteractionEvent, data: String) {
        val channel = event.channel as? ThreadChannel ?: return

        val ticketSystemTypeData = ComponentDataUtils.decode<TicketSystemTypeData>(data)
        val systemInfo = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType)
        val language = systemInfo.getI18nContext(m.languageManager)

        m.launch {
            try {
                val hook = event.interaction.reply(language.get(I18nKeysData.Tickets.ClosingYourTicket))
                    .setEphemeral(true)
                    .await()

                hook.sendMessage(language.get(I18nKeysData.Tickets.TicketClosed(event.user.asMention)))
                    .setEphemeral(false)
                    .await()

                channel.manager.setArchived(true)
                    .reason("Archival request via button by ${event.user.name} (${event.user.idLong})")
                    .await()
            } catch (e: Exception) {
                logger.warn(e) { "Something went wrong while trying to close a ticket!" }
            }
        }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        logger.info { "Select Menu Interaction ${event.user.idLong} - ${event.channel.idLong}: ${event.componentId}" }

        m.launch {
            try {
                val systemInfo = m.ticketUtils.systems[event.channel.idLong]!!
                if (systemInfo !is HelpDeskTicketSystem)
                    return@launch
                val channelResponses = systemInfo.channelResponses
                val i18nContext = systemInfo.getI18nContext(m.languageManager)

                val firstValue = event.values.first()
                if (firstValue == MY_QUESTION_ISNT_HERE_SPECIAL_KEY) {
                    if (systemInfo is SparklyPowerHelpDeskTicketSystem) {
                        event.interaction.reply(
                            MessageCreate {
                                content = listOf(
                                    LorittaReply(
                                        "Não encontrou a sua pergunta? Então tente procurar no <#${systemInfo.faqChannelId}>!",
                                        "<:pantufa_reading:853048447169986590>"
                                    ),
                                    LorittaReply(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.CreateATicketIfQuestionWasntFound(
                                                "<@&${systemInfo.supportRoleId}>",
                                            )
                                        ),
                                        "<:pantufa_comfy:853048447254396978>"
                                    )
                                ).joinToString("\n") { it.build() }

                                actionRow(
                                    Button.of(
                                        net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.PRIMARY,
                                        "create_ticket:${
                                            ComponentDataUtils.encode(
                                                TicketSystemTypeData(systemInfo.systemType)
                                            )
                                        }",
                                        i18nContext.get(I18nKeysData.Tickets.CreateTicket)
                                    ).withEmoji(Emoji.fromUnicode("➕"))
                                )
                            }
                        ).setEphemeral(true).await()
                    } else {
                        event.interaction.reply(
                            MessageCreate {
                                content = listOf(
                                    LorittaReply(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.LookUpInTheFAQIfQuestionWasntFound(
                                                "<#${systemInfo.faqChannelId}>",
                                                "<https://loritta.website/extras>",
                                            )
                                        ),
                                        "<:lori_reading:853052040430878750>"
                                    ),
                                    LorittaReply(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.CreateATicketIfQuestionWasntFound(
                                                "<@&${systemInfo.supportRoleId}>",
                                            )
                                        ),
                                        "<:lori_comfy:726873685021163601>"
                                    )
                                ).joinToString("\n") { it.build() }

                                actionRow(
                                    Button.of(
                                        net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.PRIMARY,
                                        "create_ticket:${
                                            ComponentDataUtils.encode(
                                                TicketSystemTypeData(systemInfo.systemType)
                                            )
                                        }",
                                        i18nContext.get(I18nKeysData.Tickets.CreateTicket)
                                    ).withEmoji(Emoji.fromUnicode("➕"))
                                )
                            }
                        ).setEphemeral(true).await()
                    }
                } else {
                    val response = channelResponses.first { it::class.simpleName == firstValue }

                    // Workaround because the LorittaResponse requires a content (it is only used for the "HelpMeResponse")
                    // So let's just use "button" as the content because it doesn't matter
                    val automatedSupportResponse = response.getSupportResponse("button")
                    val replies = automatedSupportResponse.replies

                    event.interaction.reply(replies.joinToString("\n") { it.build(event.user) })
                        .setEphemeral(true)
                        .await()

                    transaction(m.databases.helperDatabase) {
                        SelectedResponsesLog.insert {
                            it[timestamp] = Instant.now()
                            it[ticketSystemType] = systemInfo.systemType
                            it[userId] = event.user.idLong
                            it[selectedResponse] = firstValue
                        }
                    }
                }
            } catch (e: Exception) {
                logger.warn(e) { "Something went wrong while trying to process a pre-defined Helper response!" }
            }
        }
    }
}