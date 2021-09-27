package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.CloseTicketExecutor

object CloseTicketCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "closeticket",
        "Closes a ticket"
    ) {
        executor = CloseTicketExecutor
    }
}
