package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.FindTicketExecutor
import net.perfectdreams.loritta.helper.utils.slash.TicketInfoExecutor

class TicketUtilsCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "ticketutils",
        "Ferramentas de administração relacionadas ao sistema de tickets"
    ) {
        subcommand("find", "Encontra o ticket de um usuário") {
            executor = FindTicketExecutor(helper)
        }

        subcommand("info", "Informações sobre o cache de tickets") {
            executor = TicketInfoExecutor(helper)
        }
    }
}