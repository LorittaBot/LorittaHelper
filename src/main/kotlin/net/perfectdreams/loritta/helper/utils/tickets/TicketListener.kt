package net.perfectdreams.loritta.helper.utils.tickets

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.generics.getChannel
import dev.minn.jda.ktx.messages.MessageCreate
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.tables.StartedSupportSolicitations
import net.perfectdreams.loritta.helper.tables.TicketMessagesActivity
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.tickets.systems.HelpDeskTicketSystem
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class TicketListener(private val helper: LorittaHelper) {
    suspend fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.author.isBot)
            return

        val channelId = event.message.channel.idLong
        val channel = helper.jda.getThreadChannelById(channelId) ?: return
        if (channel.isPublic)
            return

        val parentChannel = channel.parentChannel

        val systemInfo = helper.ticketUtils.systems[parentChannel.idLong]!!
        if (systemInfo !is HelpDeskTicketSystem)
            return

        // Track user message
        transaction(helper.databases.helperDatabase) {
            val startedSupportSolicitation = StartedSupportSolicitations.selectAll()
                .where { StartedSupportSolicitations.threadId eq event.channel.idLong }
                .orderBy(StartedSupportSolicitations.startedAt, SortOrder.DESC)
                .limit(1)
                .firstOrNull()

            if (startedSupportSolicitation != null) {
                TicketMessagesActivity.insert {
                    it[TicketMessagesActivity.userId] = event.message.author.idLong
                    it[TicketMessagesActivity.messageId] = event.message.idLong
                    it[TicketMessagesActivity.timestamp] = Instant.now()
                    it[TicketMessagesActivity.supportSolicitationId] = startedSupportSolicitation[StartedSupportSolicitations.id]
                }
            }
        }

        val channelResponses = systemInfo.channelResponses
        val i18nContext = systemInfo.getI18nContext(helper.languageManager)

        // We remove any lines starting with > (quote) because this sometimes causes responses to something inside a citation, and that looks kinda bad
        val cleanMessage = event.message.contentRaw.lines()
            .dropWhile { it.startsWith(">") }
            .joinToString("\n")

        val automatedSupportResponse = channelResponses
            .firstOrNull { it.handleResponse(cleanMessage) }?.getSupportResponse(cleanMessage) ?: return
        val responses = automatedSupportResponse.replies

        if (responses.isNotEmpty())
            channel.sendMessage(
                MessageCreate {
                    if (!automatedSupportResponse.includeCloseTicketCallToAction) {
                        content = buildString {
                            for (response in responses) {
                                appendLine(response.build(event.author))
                            }

                            appendLine("-# Resposta automática, se ela resolveu a sua dúvida, então feche o ticket com `/closeticket`!")
                        }

                        allowedMentionTypes = EnumSet.of(
                            Message.MentionType.EMOJI,
                            Message.MentionType.CHANNEL,
                            Message.MentionType.SLASH_COMMAND,
                        )
                    } else {
                        val pleaseCloseTheTicketReply = LorittaReply(
                            i18nContext.get(I18nKeysData.Tickets.AutoResponseSolved),
                            "<:lori_nice:726845783344939028>",
                            mentionUser = false
                        )

                        content = (responses + pleaseCloseTheTicketReply)
                            .joinToString("\n")
                            { it.build(event.author) }

                        allowedMentionTypes = EnumSet.of(
                            Message.MentionType.EMOJI,
                            Message.MentionType.CHANNEL,
                            Message.MentionType.SLASH_COMMAND,
                        )

                        actionRow(
                            Button.of(
                                ButtonStyle.PRIMARY,
                                "close_ticket:${ComponentDataUtils.encode(TicketSystemTypeData(systemInfo.systemType))}",
                                i18nContext.get(I18nKeysData.Tickets.CloseTicket)
                            ).withEmoji(Emoji.fromCustom("lori_nice", 726845783344939028L, false)),
                        )
                    }
                }
            ).setMessageReference(event.messageIdLong).failOnInvalidReply(false).await()
    }
}