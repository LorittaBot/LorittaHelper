package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.CloseTicketExecutor

object CloseTicketCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "closeticket",
        "Closes a ticket"
    ) {
        executor = CloseTicketExecutor
    }
}
