package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclarationBuilder
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.IPLocationExecutor

object IPLocationCommand : SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "findthemeliante",
        "Em busca de meliantes pelo address"
    ) {
        executor = IPLocationExecutor
    }
}