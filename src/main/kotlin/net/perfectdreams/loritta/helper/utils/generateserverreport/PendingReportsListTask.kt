package net.perfectdreams.loritta.helper.utils.generateserverreport

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageType
import net.perfectdreams.loritta.helper.listeners.ApproveReportsOnReactionListener
import net.perfectdreams.loritta.helper.utils.Constants
import java.time.Instant
import java.time.ZoneId

class PendingReportsListTask(val jda: JDA) : Runnable {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun run() {
        val now = Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))

        try {
            // good night :3
            if (now.hour in 0..9)
                return

            val channel = jda.getTextChannelById(GenerateServerReport.SERVER_REPORTS_CHANNEL_ID) ?: return
            val staffChannel = jda.getTextChannelById(Constants.PORTUGUESE_STAFF_CHANNEL_ID) ?: return

            val history = channel.history
            var dayOfTheLastMessageInTheChannel: Int? = null

            val messages = mutableListOf<Message>()

            while (true) {
                val newMessages = history.retrievePast(100).complete()
                if (newMessages.isEmpty())
                    break

                if (dayOfTheLastMessageInTheChannel == null)
                    dayOfTheLastMessageInTheChannel = newMessages.first()
                            .timeCreated
                            .dayOfMonth

                val onlyMessagesInTheSameDay = newMessages.filter {
                    it.timeCreated.dayOfMonth == dayOfTheLastMessageInTheChannel
                }

                logger.info { "There are ${onlyMessagesInTheSameDay.size} messages that were sent in $dayOfTheLastMessageInTheChannel!" }

                if (onlyMessagesInTheSameDay.isEmpty())
                    break

                messages += onlyMessagesInTheSameDay
            }

            val messagesThatDoesNotHaveAnyReactions = messages
                    .filter { it.author.idLong == jda.selfUser.idLong }
                    .filter { it.type == MessageType.DEFAULT }
                    // Any that doesn't has only one reaction count
                    .filterNot {
                        // Filters only messages that do have a reaction that is not by helper
                        it.reactions.any {
                            // Gets if there is a reaction that is not by helper
                            it.retrieveUsers().complete().any {
                                !it.isBot
                            }
                        }
                    }

            logger.info { "There are ${messagesThatDoesNotHaveAnyReactions.size} pending reports to be seen!" }

            if (messagesThatDoesNotHaveAnyReactions.isNotEmpty())
                staffChannel.sendMessage("""<a:walter_contra_bonoro:729116259446161448> **ATENÇÃO <@&351473717194522647>**
                    |<a:uniao:703352880320479272> Existem ${messagesThatDoesNotHaveAnyReactions.size} denúncias que ainda precisam ser vistas!
                    |<a:wumpus_keyboard:682249824133054529> **Lembre-se:** ${ApproveReportsOnReactionListener.APPROVE_EMOTE} aceita a denúncia e ${ApproveReportsOnReactionListener.REJECT_EMOTE} rejeita a denúncia (Mas as punições ainda precisam ser dadas manualmente!). Qualquer outra reação pode ser utilizada para ignorar os avisos de denúncia pendente!
                    |
                    |${messagesThatDoesNotHaveAnyReactions.joinToString("\n") { it.jumpUrl }}
                """.trimMargin()
                ).queue()
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while trying to retrieve the reports!" }
        }
    }
}