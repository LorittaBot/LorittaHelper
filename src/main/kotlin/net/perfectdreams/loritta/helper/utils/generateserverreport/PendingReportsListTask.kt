package net.perfectdreams.loritta.helper.utils.generateserverreport

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.perfectdreams.loritta.helper.listeners.ApproveReportsOnReactionListener
import net.perfectdreams.loritta.helper.utils.Constants

class PendingReportsListTask(val jda: JDA) : Runnable {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun run() {
        try {
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

            val messagesThatDoesNotHaveAnyReactions = messages.filter { it.reactions.isEmpty() }

            logger.info { "There are ${messagesThatDoesNotHaveAnyReactions.size} pending reports to be seen!" }

            if (messagesThatDoesNotHaveAnyReactions.isNotEmpty())
                staffChannel.sendMessage("<a:walter_contra_bonoro:729116259446161448> <@&351473717194522647> Existem ${messagesThatDoesNotHaveAnyReactions.size} denúncias que ainda precisam ser vistas! **Lembre-se:** ${ApproveReportsOnReactionListener.APPROVE_EMOTE} aceita a denúncia e ${ApproveReportsOnReactionListener.REJECT_EMOTE} rejeita a denúncia (Mas as punições ainda precisam ser dadas manualmente!). Qualquer outra reação pode ser utilizada para ignorar os avisos de denúncia pendente!")
                        .queue()
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while trying to retrieve the reports!" }
        }
    }
}