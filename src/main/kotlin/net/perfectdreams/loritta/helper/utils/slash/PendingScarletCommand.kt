package net.perfectdreams.loritta.helper.utils.slash

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.dailycatcher.SuspiciousLevel
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.sequins.text.StringUtils

class PendingScarletCommand(helper: LorittaHelper, val jda: JDA) : HelperSlashCommand(helper, this) {
    companion object : SlashCommandDeclaration(
        name = "pendingscarlet",
        description = "Scarlet Police, on Ghetto Patrol \uD83D\uDC83"
    ) {
        private val logger = KotlinLogging.logger {}
    }

    override suspend fun executesHelper(context: SlashCommandContext) {
        val channel = jda.getTextChannelById(DailyCatcherManager.SCARLET_POLICE_CHANNEL_ID) ?: return

        try {
            val history = channel.history
            var dayOfTheLastMessageInTheChannel: Int? = null

            val messages = mutableListOf<Message>()

            while (true) {
                val newMessages = history.retrievePast(100).await()
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

            logger.info { "There are ${messages.size} messages to be sent!" }

            // Do a filter of the ones that aren't approved yet
            val notApprovedMessages = messages.filter {
                // Has the "ban" emote but does not have the "catpolice" emote
                it.getReactionById(750509326782824458L) != null && it.getReactionById(585608392110899200L) == null
            }

            // Now sort them by sus level
            val susLevelMessages = mutableMapOf<SuspiciousLevel, MutableList<Message>>()

            notApprovedMessages.forEach {
                val susLevelMessage = it.contentRaw.lines().firstOrNull { it.contains("**Nível de sus:** ") }

                if (susLevelMessage != null) {
                    val emote = susLevelMessage.split(" ")
                        .first()

                    val suspiciousLevelByEmote = SuspiciousLevel.values().first { it.emote == emote }
                    val list = susLevelMessages.getOrPut(suspiciousLevelByEmote) {
                        mutableListOf()
                    }

                    list.add(it)

                    susLevelMessages[suspiciousLevelByEmote] = list
                }
            }

            val lines = mutableListOf(
                "**Lista dos reports pendentes de hoje:**\n"
            )

            // we are going to sort them by the sus level (higher -> lower)
            susLevelMessages.entries.sortedByDescending { it.key.level }.forEach { (t, u) ->
                lines.add(t.emote + "\n")

                u.forEach {
                    lines.add(it.jumpUrl + "\n")
                }

                lines.add("\n\n")
            }

            val chunkedLines = StringUtils.chunkedLines(lines, 1900, forceSplit = true)

            // And now send them!
            for (line in chunkedLines) {
                context.sendMessage {
                    content = line
                }
            }
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while trying to retrieve the reports!" }
        }
    }
}