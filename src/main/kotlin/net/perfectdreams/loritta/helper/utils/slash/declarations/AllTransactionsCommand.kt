package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.AllTransactionsExecutor

class AllTransactionsCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "alltransactions",
        "Pega todas as transações do usuário e envia um arquivo com todas elas"
    ) {
        executor = AllTransactionsExecutor(helper)
    }
}
