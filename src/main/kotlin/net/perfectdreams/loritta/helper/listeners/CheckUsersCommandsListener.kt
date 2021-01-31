package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.dailycatcher.SuspiciousLevel
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.sequins.text.StringUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.concurrent.thread

class CheckUsersCommandsListener(val m: LorittaHelper) : ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)

        val member = event.member ?: return

        if (!member.roles.any { it.idLong == 351473717194522647L })
            return

        val args = event.message.contentRaw.split(" ")

        if (event.message.contentRaw.startsWith("h!daily_catcher_check")) {
            thread {
                m.dailyCatcherManager?.doReports()
            }
        } else if (event.message.contentRaw.startsWith("h!check_commands")) {
            val userId = args[1].toLong()

            m.launch {
                val commandCountField = ExecutedCommandsLog.command.count()

                val commands = transaction(m.databases.lorittaDatabase) {
                    ExecutedCommandsLog.slice(ExecutedCommandsLog.command, commandCountField)
                            .select {
                                ExecutedCommandsLog.userId eq userId
                            }
                            .groupBy(ExecutedCommandsLog.command)
                            .orderBy(commandCountField, SortOrder.DESC)
                            .limit(15)
                            .toList()
                }

                var input = "**Stats de comandos de ${userId}**\n"
                input += "**Quantidade de comandos executados:** ${commands.sumBy { it[commandCountField].toInt() }}\n"
                input += "**Comandos de economia executados:** ${
                    commands.filter { it[ExecutedCommandsLog.command] in DailyCatcherManager.ECONOMY_COMMANDS }
                            .sumBy { it[commandCountField].toInt() }
                }\n"
                input += "\n"

                for (command in commands) {
                    input += "**`${command[ExecutedCommandsLog.command]}`:** ${command[commandCountField]}\n"
                }

                event.channel.sendMessage(input)
                        .queue()
            }
        } else if (event.message.contentRaw.startsWith("h!pending_scarlet")) {
            val channel = event.jda.getTextChannelById(DailyCatcherManager.SCARLET_POLICE_CHANNEL_ID) ?: return

            m.launch {
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
                        event.channel.sendMessage(line).await()
                    }
                } catch (e: Exception) {
                    logger.warn(e) { "Something went wrong while trying to retrieve the reports!" }
                }
            }
        }
    }
}