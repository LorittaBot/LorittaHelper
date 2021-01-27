package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcher
import net.perfectdreams.loritta.helper.utils.dailycatcher.SuspiciousLevel
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.extensions.retrieveAllMessages
import net.perfectdreams.sequins.text.StringUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CheckUsersCommandsListener(val m: LorittaHelper) : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)

        val member = event.member ?: return

        if (!member.roles.any { it.idLong == 351473717194522647L })
            return

        val args = event.message.contentRaw.split(" ")

        if (event.message.contentRaw.startsWith("h!check_commands")) {
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
                    commands.filter { it[ExecutedCommandsLog.command] in DailyCatcher.ECONOMY_COMMANDS }
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
            val channel = event.jda.getTextChannelById(DailyCatcher.SCARLET_POLICE_CHANNEL_ID) ?: return

            m.launch {
                val allMessages = channel.history.retrieveAllMessages()

                // Do a filter of the ones that aren't approved yet
                val notApprovedMessages = allMessages.filter {
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

                val lines = mutableListOf<String>()

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
            }
        }
    }
}