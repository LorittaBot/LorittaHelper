package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CheckCommandsExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val user = user("user", "Usuário a ser verificado")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()
        val user = args[options.user]

        val commandCountField = ExecutedCommandsLog.command.count()

        val commands = transaction(helper.databases.lorittaDatabase) {
            ExecutedCommandsLog.slice(ExecutedCommandsLog.command, commandCountField)
                .select {
                    ExecutedCommandsLog.userId eq user.id.value.toLong()
                }
                .groupBy(ExecutedCommandsLog.command)
                .orderBy(commandCountField, SortOrder.DESC)
                .limit(15)
                .toList()
        }

        var input = "**Stats de comandos de ${user.id.value}**\n"
        input += "**Quantidade de comandos executados:** ${commands.sumBy { it[commandCountField].toInt() }}\n"
        input += "**Comandos de economia executados:** ${
            commands.filter { it[ExecutedCommandsLog.command] in DailyCatcherManager.ECONOMY_COMMANDS }
                .sumBy { it[commandCountField].toInt() }
        }\n"
        input += "\n"

        for (command in commands) {
            input += "**`${command[ExecutedCommandsLog.command]}`:** ${command[commandCountField]}\n"
        }

        context.sendMessage {
            content = input
        }
    }
}