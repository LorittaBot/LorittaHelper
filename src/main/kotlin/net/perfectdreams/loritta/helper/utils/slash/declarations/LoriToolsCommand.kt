package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.LoriBanExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriBanRenameExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriUnbanExecutor

object LoriToolsCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "loritools",
        "Ferramentas de administração relacionadas a Loritta"
    ) {
        subcommand("loriban", "Bane alguém de usar a Loritta") {
            executor = LoriBanExecutor
        }

        subcommand("loriunban", "Desbane alguém de usar a Loritta") {
            executor = LoriUnbanExecutor
        }

        subcommand("loribanrename", "Altera o motivo do ban de um usuário") {
            executor = LoriBanRenameExecutor
        }
    }
}