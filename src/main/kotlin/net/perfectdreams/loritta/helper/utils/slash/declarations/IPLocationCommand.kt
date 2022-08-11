package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.IPLocationExecutor

class IPLocationCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "findthemeliante",
        "Em busca de meliantes pelo address"
    ) {
        executor = IPLocationExecutor(helper)
    }
}