package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.TicketSenderExecutor

object TicketSenderCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "ticketsender",
        "Envia a mensagem de tickets no canal selecionado"
    ) {
        executor = TicketSenderExecutor
    }
}
