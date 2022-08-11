package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.dv8tion.jda.api.JDA
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.PendingReportsExecutor

class PendingReportsCommand(val helper: LorittaHelperKord, val jda: JDA) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "pendingreports",
        "Veja os reports/apelos pendentes do mês! \uD83D\uDC6E")
    {
        executor = PendingReportsExecutor(helper, jda)
    }
}