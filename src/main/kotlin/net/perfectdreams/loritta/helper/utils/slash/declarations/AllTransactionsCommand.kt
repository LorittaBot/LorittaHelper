package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.AllTransactionsExecutor

object AllTransactionsCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "alltransactions",
        "Pega todas as transações do usuário e envia um arquivo com todas elas"
    ) {
        executor = AllTransactionsExecutor
    }
}
