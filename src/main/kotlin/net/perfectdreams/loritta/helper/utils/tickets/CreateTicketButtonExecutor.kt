package net.perfectdreams.loritta.helper.utils.tickets

import com.github.benmanes.caffeine.cache.Caffeine
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optional
import dev.kord.core.entity.User
import dev.kord.rest.builder.channel.thread.ThreadModifyBuilder
import dev.kord.rest.json.request.StartThreadRequest
import mu.KotlinLogging
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutor
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.GuildComponentContext
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.tables.StartedSupportSolicitations
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.concurrent.TimeUnit

class CreateTicketButtonExecutor(val m: LorittaHelperKord) : ButtonExecutor {
    companion object : ButtonExecutorDeclaration("create_ticket") {
        private val logger = KotlinLogging.logger {}
    }

    val recentlyCreatedTickets = Caffeine.newBuilder()
        .expireAfterWrite(5L, TimeUnit.MINUTES)
        .build<Snowflake, Long>()
        .asMap()

    override suspend fun onClick(user: User, context: ComponentContext) {
        if (context is GuildComponentContext) {
            // Check if user is banned from Loritta, because it is super annoying them creating tickets just to ask them to be unbanned
            val currentBanState = transaction(m.databases.lorittaDatabase) {
                BannedUsers.select {
                    BannedUsers.userId eq user.id.value.toLong() and
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

            if (currentBanState != null) {
                context.deferUpdateMessage()

                val permRoleId = Snowflake(781591507849052200L)
                val tempRoleId = Snowflake(781591507849052200L)

                if (currentBanState[BannedUsers.expiresAt] != null) {
                    if (context.member.roleIds.contains(permRoleId)) {
                        m.helperRest.guild.deleteRoleFromGuildMember(
                            Snowflake(Constants.SUPPORT_SERVER_ID),
                            user.id,
                            permRoleId
                        )
                    }

                    if (!context.member.roleIds.contains(tempRoleId)) {
                        m.helperRest.guild.addRoleToGuildMember(
                            Snowflake(Constants.SUPPORT_SERVER_ID),
                            user.id,
                            tempRoleId
                        )
                    }
                } else {
                    if (context.member.roleIds.contains(tempRoleId)) {
                        m.helperRest.guild.deleteRoleFromGuildMember(
                            Snowflake(Constants.SUPPORT_SERVER_ID),
                            user.id,
                            tempRoleId
                        )
                    }

                    if (!context.member.roleIds.contains(permRoleId)) {
                        m.helperRest.guild.addRoleToGuildMember(
                            Snowflake(Constants.SUPPORT_SERVER_ID),
                            user.id,
                            permRoleId
                        )
                    }
                }
                return
            }

            val ticketSystemTypeData = ComponentDataUtils.decode<TicketSystemTypeData>(context.data)
            val systemInfo = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType)
            val language = systemInfo.getI18nContext(m.languageManager)

            // Avoid users closing and reopening threads constantly
            val lastTicketCreatedAt = recentlyCreatedTickets[user.id]

            if (systemInfo.systemType == TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE && context.member.roleIds.contains(Snowflake(341343754336337921L))) { // Desenhistas role
                context.sendEphemeralMessage {
                    // 300 = 5 minutes
                    content = language.get("Você já tem o cargo de desenhistas, você não precisa enviar uma \"Primeira Fan Art\" novamente! Caso queira enviar mais fan arts para a galeria, basta enviar em <#583406099047252044>")
                }
                return
            }

            if (lastTicketCreatedAt != null) {
                context.sendEphemeralMessage {
                    // 300 = 5 minutes
                    content = language.get(
                        I18nKeysData.Tickets.YouAlreadyCreatedATicketRecently(
                            "<:lori_sob:556524143281963008>",
                            "<t:${(lastTicketCreatedAt / 1000) + 300}:R>"
                        )
                    )
                }
                return
            }
            recentlyCreatedTickets[user.id] = System.currentTimeMillis()

            context.sendEphemeralMessage {
                content = language.get(I18nKeysData.Tickets.CreatingATicket)
            }

            val cachedTickets = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType).cache
            val alreadyCreatedUserTicketData = cachedTickets.tickets[context.sender.id]
            var ticketThreadId = alreadyCreatedUserTicketData?.id

            // Max username size = 32
            // Max ID length (well it can be bigger): 18
            // So if we do the sum of everything...
            // 3 (beginning) + 32 (username) + 2 (space and "(") + 18 (user ID) + 1 (")")
            // = 56
            // Threads can have at most 100 chars!
            val threadName = "\uD83D\uDCE8 ${user.username} (${user.id.value})"

            if (alreadyCreatedUserTicketData == null) {
                // If it is STILL null, we will create a thread!
                ticketThreadId = m.helperRest.channel.startThread(
                    context.channelId,
                    StartThreadRequest(
                        threadName,
                        systemInfo.archiveDuration,
                        ChannelType.PrivateThread.optional(),
                    ),
                    "Ticket created for <@${user.id.value}>"
                ).id
            }

            if (ticketThreadId == null) {
                logger.warn { "ticketThreadId is null, this should never happen! Invalidating cached ticket ID and retrying..." }
                recentlyCreatedTickets[user.id] = null
                cachedTickets.tickets.remove(context.sender.id)
                onClick(user, context)
                return
            }

            // Update thread metadata and name juuuust to be sure
            m.helperRest.channel.patchThread(
                ticketThreadId,
                ThreadModifyBuilder().apply {
                    this.name = threadName
                    this.archived = false
                    // Before we used locked = false due to a bug in Discord Mobile related to "You don't have permission!", however it seems to have been fixed, so we don't need to keep it unlocked
                    // Besides, allowing unlock makes people confused, because they click to close the ticket but they can still send messages in the ticket
                    this.locked = true
                    this.invitable = false
                }.toRequest(),
                "Unarchival request via button by ${user.username}#${user.discriminator} (${user.id.value})"
            )

            // We need to add the user to the thread after it is unarchived!
            m.helperRest.channel.addUserToThread(
                ticketThreadId,
                user.id
            )

            cachedTickets.tickets[user.id] = TicketsCache.DiscordThreadTicketData(ticketThreadId)

            transaction(m.databases.helperDatabase) {
                StartedSupportSolicitations.insert {
                    it[StartedSupportSolicitations.userId] = context.sender.id.value.toLong()
                    it[StartedSupportSolicitations.startedAt] = Instant.now()
                    it[StartedSupportSolicitations.threadId] = ticketThreadId.value.toLong()
                    it[StartedSupportSolicitations.systemType] = ticketSystemTypeData.systemType
                }
            }

            // Only resend the message if the thread was archived or if it is a new thread
            m.helperRest.channel.createMessage(
                ticketThreadId
            ) {
                systemInfo.ticketCreatedMessage.invoke(this, context.sender, language)
            }

            context.sendEphemeralMessage {
                content = language.get(
                    I18nKeysData.Tickets.TicketWasCreated("<#${ticketThreadId}>")
                )
            }
        }
    }
}