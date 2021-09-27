package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.TicketSenderExecutor

object TicketSenderCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "ticketsender",
        "Envia a mensagem de tickets no canal selecionado"
    ) {
        executor = TicketSenderExecutor
    }
}
