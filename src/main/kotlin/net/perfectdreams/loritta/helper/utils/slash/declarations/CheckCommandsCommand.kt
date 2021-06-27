package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclarationBuilder
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.CheckCommandsExecutor

object CheckCommandsCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "checkcommands",
        "Verifica quais comandos um usuário mais usa"
    ) {
        executor = CheckCommandsExecutor
    }

}