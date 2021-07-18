package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.generateserverreport.GenerateAppealsReport

class ApproveAppealsOnReactionListener(val m: LorittaHelper): ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
        const val APPROVE_EMOTE = "✅"
        const val REJECT_EMOTE = "\uD83D\uDEAB"
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.user.isBot)
            return

        if (event.channel.idLong != GenerateAppealsReport.APPEALS_CHANNEL_ID)
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
                                    it.sendMessage("""O seu apelo foi aceito pela equipe e você foi desbanido! <:lori_feliz:519546310978830355>
                                    |
                                    |Isso é algo raro, então não jogue essa oportunidade no lixo. Respeite as regras da Loritta para não ser banido novamente! <:lori_nice:726845783344939028>
                                    |
                                    |https://tenor.com/bqUXw.gif
                                """.trimMargin())
                                            .queue()
                                } else if (event.reactionEmote.name == REJECT_EMOTE) {
                                    // Rejected
                                    it.sendMessage("""O seu apelo para ser desbanido na Loritta foi rejeitado pela equipe""")
                                            .queue()
                                }
                            }
                        }
            }
        }
    }
}