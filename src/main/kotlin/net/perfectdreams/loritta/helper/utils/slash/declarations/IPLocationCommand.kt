package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.IPLocationExecutor

object IPLocationCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "findthemeliante",
        "Em busca de meliantes pelo address"
    ) {
        executor = IPLocationExecutor
    }
}