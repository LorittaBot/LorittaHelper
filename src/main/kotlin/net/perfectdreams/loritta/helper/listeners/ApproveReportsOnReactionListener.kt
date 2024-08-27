package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.StaffProcessedReports
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.StaffProcessedReportResult
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.generateserverreport.GenerateServerReport
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class ApproveReportsOnReactionListener(val m: LorittaHelper): ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
        val APPROVE_EMOTE = Emoji.fromUnicode("✅")
        val REJECT_EMOTE = Emoji.fromUnicode("\uD83D\uDEAB")
        const val THINKING_EMOTE = "\uD83E\uDD14"
    }

    private val community = m.config.guilds.community
    private val SERVER_REPORTS_CHANNEL_ID = community.channels.serverReports

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        val user = event.user ?: return
        if (user.isBot)
            return

        if (event.channel.idLong != SERVER_REPORTS_CHANNEL_ID)
            return

        if (event.emoji == APPROVE_EMOTE || event.emoji == REJECT_EMOTE) {
            m.launch {
                val retrievedMessage = event.retrieveMessage()
                        .await()

                val firstEmbed = retrievedMessage.embeds.firstOrNull() ?: return@launch

                val reporterId = firstEmbed.author?.name?.substringAfterLast("(")
                        ?.substringBeforeLast(")")
                        ?.toLongOrNull()

                if (reporterId == null) {
                    logger.info { "Not processing DM messages for message ${event.messageId} because I couldn't find who created the report!" }
                    return@launch
                }

                // Only allow reactions if only two users reacted in the message (so, the bot itself and the user)
                val reactedUsers = event.reaction.retrieveUsers()
                        .await()

                if (reactedUsers.size != 2) {
                    logger.info { "Not processing DM messages for message ${event.messageId} because there is already two reactions" }
                    return@launch
                }

                transaction(m.databases.helperDatabase) {
                    StaffProcessedReports.insert {
                        it[StaffProcessedReports.timestamp] = Instant.now()
                        it[StaffProcessedReports.userId] = event.userIdLong
                        it[StaffProcessedReports.reporterId] = reporterId
                        it[StaffProcessedReports.messageId] = event.messageIdLong
                        it[StaffProcessedReports.result] = if (event.emoji == APPROVE_EMOTE)
                            StaffProcessedReportResult.APPROVED
                        else StaffProcessedReportResult.REJECTED
                    }
                }

                event.jda.retrieveUserById(reporterId)
                        .queue {
                            it.openPrivateChannel().queue {
                                if (event.emoji == APPROVE_EMOTE) {
                                    // Approved
                                    it.sendMessage("""A sua denúncia foi aceita pela equipe e os meliantes da denúncia foram punidos! <:lori_feliz:519546310978830355>
                                    |
                                    |Suas denúncias ajudam o servidor da Loritta, a Loritta e servidores da LorittaLand a serem um lugar melhor, então, obrigada por colaborar e ajudar a nossa equipe a punir esses meliantes safados! <:lori_nice:726845783344939028>
                                    |
                                    |https://tenor.com/bqUXw.gif
                                """.trimMargin())
                                            .queue()
                                } else if (event.emoji == REJECT_EMOTE) {
                                    // Rejeted
                                    var rejectReason = "Se você quiser saber o motivo da sua denúncia ter sido rejeitada, é melhor perguntar para a equipe! Eu sou apenas um bot, não sei o motivo... <:lori_flushed:732706868224327702>"
                                    if (firstEmbed.fields.any { it.name == "Resposta da Staff" }) {
                                        val reasonField = firstEmbed.fields.find { it.name == "Resposta da Staff" }
                                        rejectReason = "A equipe anexou o seguinte motivo pela qual a denúncia foi negada: `${reasonField?.value}` ${Emotes.LORI_COFFEE}"
                                    }
                                    it.sendMessage("""A sua denúncia foi rejeitada pela equipe... provavelmente porque a denúncia que você enviou não é algo contra as regras, ou está faltando provas, ou a pessoa já tinha sido punida... tem vários motivos porque a gente pode ter rejeitado a sua denúncia!
                                        |
                                        |${rejectReason}
                                """.trimMargin())
                                            .queue()
                                }
                            }
                        }
            }
        }
    }
}