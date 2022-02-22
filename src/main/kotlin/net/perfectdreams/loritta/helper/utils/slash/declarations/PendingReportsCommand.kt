package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.PendingReportsExecutor

object PendingReportsCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "pendingreports",
        "Veja os reports/apelos pendentes do mês! \uD83D\uDC6E")
    {
        executor = PendingReportsExecutor
    }
}