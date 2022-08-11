package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.StatsReportsExecutor
import net.perfectdreams.loritta.helper.utils.slash.StatsTicketsExecutor

class StatsCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
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