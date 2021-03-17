package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.commands.get
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.required
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CheckCommandsCommand(helper: LorittaHelper) : HelperSlashCommand(helper, this) {
    companion object : SlashCommandDeclaration(
        name = "checkcommands",
        description = "Verifica quais comandos um usuário mais usa"
    ) {
        override val options = Options

        object Options : SlashCommandDeclaration.Options() {
            val user = user("user", "Usuário a ser verificado")
                .required()
                .register()
        }
    }

    override suspend fun executesHelper(context: SlashCommandContext) {
        val user = options.user.get(context)

        val commandCountField = ExecutedCommandsLog.command.count()

        val commands = transaction(helper.databases.lorittaDatabase) {
            ExecutedCommandsLog.slice(ExecutedCommandsLog.command, commandCountField)
                .select {
                    ExecutedCommandsLog.userId eq user.id.value
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