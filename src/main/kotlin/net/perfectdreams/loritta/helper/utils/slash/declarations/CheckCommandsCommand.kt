package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.CheckCommandsExecutor

object CheckCommandsCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "checkcommands",
        "Verifica quais comandos um usuário mais usa"
    ) {
        executor = CheckCommandsExecutor
    }

}