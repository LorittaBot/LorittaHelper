package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.StatsReportsExecutor
import net.perfectdreams.loritta.helper.utils.slash.StatsTicketsExecutor

class StatsCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "stats",
        "Estatísticas para a equipe da garotinha mais fof do mundo")
    {
        subcommand("reports", "Estatísticas sobre denúncias") {
            executor = StatsReportsExecutor(helper)
        }

        subcommand("tickets", "Estatísticas sobre tickets") {
            executor = StatsTicketsExecutor(helper)
        }
    }
}