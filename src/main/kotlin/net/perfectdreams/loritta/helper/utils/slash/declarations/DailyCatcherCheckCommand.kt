package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.DailyCatcherCheckExecutor

class DailyCatcherCheckCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "dailycatchercheck",
    "Contas Fakes, temos que pegar!") {
        executor = DailyCatcherCheckExecutor(helper)
    }
}