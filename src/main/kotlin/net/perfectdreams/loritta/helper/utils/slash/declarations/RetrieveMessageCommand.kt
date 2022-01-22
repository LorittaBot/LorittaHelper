package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.RetrieveMessageExecutor

object RetrieveMessageCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "retrievemessage",
    "Pega o conteúdo de uma mensagem a partir de um link") {
        executor = RetrieveMessageExecutor
    }
}