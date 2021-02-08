package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.generateserverreport.GenerateServerReport

class ApproveReportsOnReactionListener(val m: LorittaHelper): ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val APPROVE_EMOTE = "✅"
        const val REJECT_EMOTE = "\uD83D\uDEAB"
        const val THINKING_EMOTE = "\uD83E\uDD14"
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.user.isBot)
            return

        if (event.channel.idLong != GenerateServerReport.SERVER_REPORTS_CHANNEL_ID)
            return

        if (event.reactionEmote.name == APPROVE_EMOTE || event.reactionEmote.name == REJECT_EMOTE) {
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

                event.jda.retrieveUserById(reporterId)
                        .queue {
                            it.openPrivateChannel().queue {
                                if (event.reactionEmote.name == APPROVE_EMOTE) {
                                    // Approved
                                    it.sendMessage("""A sua denúncia foi aceita pela equipe e os meliantes da denúncia foram punidos! <:lori_feliz:519546310978830355>
                                    |
                                    |Suas denúncias ajudam o servidor da Loritta, a Loritta e servidores da LorittaLand a serem um lugar melhor, então, obrigada por colaborar e ajudar a nossa equipe a punir esses meliantes safados! <:lori_nice:726845783344939028>
                                    |
                                    |https://tenor.com/bqUXw.gif
                                """.trimMargin())
                                            .queue()
                                } else if (event.reactionEmote.name == REJECT_EMOTE) {
                                    // Rejected
                                    it.sendMessage("""A sua denúncia foi rejeitada pela equipe... provavelmente porque a denúncia que você enviou não é algo contra as regras, ou está faltando provas, ou a pessoa já tinha sido punida... tem vários motivos porque a gente pode ter rejeitado a sua denúncia!
                                        |
                                        |Se você quiser saber o motivo da sua denúncia ter sido rejeitada, é melhor perguntar para a equipe! Eu sou apenas um bot, não sei o motivo... <:lori_flushed:732706868224327702>
                                """.trimMargin())
                                            .queue()
                                }
                            }
                        }
            }
        }
    }
}