package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CheckCommandsExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(CheckCommandsExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            val user = user("user", "Usuário a ser verificado")
                .register()
        }
    }

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