package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.HelperExecutor
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CheckCommandsExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val user = user("user", "Usuário a ser verificado")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage(false)
        val user = args[options.user].user

        val commandCountField = ExecutedCommandsLog.command.count()

        val commands = transaction(helper.databases.lorittaDatabase) {
            ExecutedCommandsLog.select(ExecutedCommandsLog.command, commandCountField)
                .where { ExecutedCommandsLog.userId eq user.idLong }
                .groupBy(ExecutedCommandsLog.command)
                .orderBy(commandCountField, SortOrder.DESC)
                .limit(15)
                .toList()
        }

        var input = "**Stats de comandos de ${user.idLong}**\n"
        input += "**Quantidade de comandos executados:** ${commands.sumBy { it[commandCountField].toInt() }}\n"
        input += "**Comandos de economia executados:** ${
            commands.filter { it[ExecutedCommandsLog.command] in DailyCatcherManager.ECONOMY_COMMANDS }
                .sumBy { it[commandCountField].toInt() }
        }\n"
        input += "\n"

        for (command in commands) {
            input += "**`${command[ExecutedCommandsLog.command]}`:** ${command[commandCountField]}\n"
        }

        context.reply(false) {
            content = input
        }
    }
}