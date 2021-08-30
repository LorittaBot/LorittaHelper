package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.CheckCommandsExecutor

object CheckCommandsCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "checkcommands",
        "Verifica quais comandos um usuário mais usa"
    ) {
        executor = CheckCommandsExecutor
    }

}