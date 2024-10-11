package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.IPLocationExecutor

class IPLocationCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "findthemeliante",
        "Em busca de meliantes pelo address"
    ) {
        executor = IPLocationExecutor(helper)
    }
}