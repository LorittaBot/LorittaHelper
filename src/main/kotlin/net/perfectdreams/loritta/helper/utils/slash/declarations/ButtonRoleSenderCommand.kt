package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.ButtonRoleSenderExecutor

object ButtonRoleSenderCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "buttonrolesender",
        "Envia a mensagem de cargos no canal selecionado"
    ) {
        executor = ButtonRoleSenderExecutor
    }
}
