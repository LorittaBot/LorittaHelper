package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.LoriBanExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriBanRenameExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriEconomyStateExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriUnbanExecutor

class LoriToolsCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "loritools",
        "Ferramentas de administração relacionadas a Loritta"
    ) {
        subcommand("loriban", "Bane alguém de usar a Loritta") {
            executor = LoriBanExecutor(helper)
        }

        subcommand("loriunban", "Desbane alguém de usar a Loritta") {
            executor = LoriUnbanExecutor(helper)
        }

        subcommand("loribanrename", "Altera o motivo do ban de um usuário") {
            executor = LoriBanRenameExecutor(helper)
        }

        subcommand("economy", "Altera o estado da economia da Loritta") {
            executor = LoriEconomyStateExecutor(helper)
        }
    }
}