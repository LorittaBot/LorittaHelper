package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.AllTransactionsExecutor

object AllTransactionsCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "alltransactions",
        "Pega todas as transações do usuário e envia um arquivo com todas elas"
    ) {
        executor = AllTransactionsExecutor
    }
}
