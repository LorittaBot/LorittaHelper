package net.perfectdreams.loritta.helper.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import dev.minn.jda.ktx.messages.MessageCreate
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
        val (id, data) = event.componentId.split(":")

        if (id == "create_ticket") {
            m.launch {
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
                    return@launch

                val ticketSystemTypeData = ComponentDataUtils.decode<TicketSystemTypeData>(data)
                val systemInfo = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType)
                val language = systemInfo.getI18nContext(m.languageManager)

                val hook = event.interaction.deferReply(true).await()

                if (systemInfo.systemType == TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE && member.roles.any { it.idLong == 341343754336337921L }) { // Desenhistas role
                    hook.editOriginal("Você já tem o cargo de desenhistas, você não precisa enviar uma \"Primeira Fan Art\" novamente! Caso queira enviar mais fan arts para a galeria, basta enviar em <#583406099047252044>")
                        .await()
                    return@launch
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
                    return@launch
                }

                recentlyCreatedTickets[event.user.idLong] = System.currentTimeMillis()

                hook.editOriginal(language.get(I18nKeysData.Tickets.CreatingATicket)).await()

                val cachedTickets = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType).cache
                val alreadyCreatedUserTicketData = cachedTickets.tickets[event.user.idLong]
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
                    .firstOrNull { it.idLong == ticketThreadId } ?: run {
                    // Hack hack hack
                    event.channel.asThreadContainer().retrieveArchivedPrivateThreadChannels()
                        .skipTo(ticketThreadId - 1)
                        .limit(1)
                        .await()
                        .firstOrNull { it.idLong == ticketThreadId }
                } ?: error("Couldn't find thread channel!")

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
            }
        } else if (id == "close_ticket") {
            val channel = event.channel as? ThreadChannel ?: return

            val ticketSystemTypeData = ComponentDataUtils.decode<TicketSystemTypeData>(data)
            val systemInfo = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType)
            val language = systemInfo.getI18nContext(m.languageManager)

            m.launch {
                val hook = event.interaction.reply(language.get(I18nKeysData.Tickets.ClosingYourTicket))
                    .setEphemeral(true)
                    .await()

                hook.sendMessage(language.get(I18nKeysData.Tickets.TicketClosed(event.user.asMention)))
                    .setEphemeral(false)
                    .await()

                channel.manager.setArchived(true)
                    .reason("Archival request via button by ${event.user.name} (${event.user.idLong})")
                    .await()
            }
        }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        m.launch {
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
                    ).await()
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
                    ).await()
                }
            } else {
                val response = channelResponses.first { it::class.simpleName == firstValue }

                // Workaround because the LorittaResponse requires a content (it is only used for the "HelpMeResponse")
                // So let's just use "button" as the content because it doesn't matter
                val replies = response.getResponse("button")

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
        }
    }
}