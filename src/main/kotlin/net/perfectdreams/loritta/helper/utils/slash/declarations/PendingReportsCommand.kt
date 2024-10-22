package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.PendingReportsExecutor

class PendingReportsCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "pendingreports",
        "Veja os reports/apelos pendentes do mês! \uD83D\uDC6E")
    {
        executor = PendingReportsExecutor(helper)
    }
}