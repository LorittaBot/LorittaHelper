package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclarationBuilder
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.RetrieveMessageExecutor

object RetrieveMessageCommand : SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "retrievemessage",
    "Pega o conteúdo de uma mensagem a partir de um link") {
        executor = RetrieveMessageExecutor
    }
}