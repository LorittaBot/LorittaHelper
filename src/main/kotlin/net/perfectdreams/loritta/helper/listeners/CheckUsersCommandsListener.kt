package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcher
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CheckUsersCommandsListener(val m: LorittaHelper) : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)

        if (!event.message.contentRaw.startsWith("h!check_commands"))
            return

        val member = event.member ?: return

        if (!member.roles.any { it.idLong == 351473717194522647L })
            return

        val args = event.message.contentRaw.split(" ")
        val userId = args[1].toLong()

        m.launch {
            val commandCountField = ExecutedCommandsLog.command.count()

            val commands = transaction {
                ExecutedCommandsLog.slice(ExecutedCommandsLog.command, commandCountField)
                    .select {
                        ExecutedCommandsLog.userId eq userId
                    }
                    .groupBy(ExecutedCommandsLog.command)
                    .orderBy(commandCountField)
                    .limit(15)
                    .toList()
            }

            var input = "**Stats de comandos de ${userId}**\n"
            input += "**Quantidade de comandos executados:** ${commands.sumBy { it[commandCountField].toInt() } }}\n"
            input += "**Comandos de economia executados:** ${commands.filter { it[ExecutedCommandsLog.command] in DailyCatcher.ECONOMY_COMMANDS }.sumBy { it[commandCountField].toInt() }}\n"
            input += "\n"

            for (command in commands) {
                input += "**`${command[ExecutedCommandsLog.command]}`:** ${command[commandCountField]}"
            }

            event.channel.sendMessage(input)
                .queue()
        }
    }
}