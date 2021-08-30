package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.RetrieveMessageExecutor

object RetrieveMessageCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "retrievemessage",
    "Pega o conteúdo de uma mensagem a partir de um link") {
        executor = RetrieveMessageExecutor
    }
}