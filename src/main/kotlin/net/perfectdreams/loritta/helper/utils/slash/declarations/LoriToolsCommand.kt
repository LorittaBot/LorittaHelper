package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.LoriBanExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriUnbanExecutor

object LoriToolsCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "loritools",
        "Ferramentas de administração relacionadas a Loritta"
    ) {
        subcommand("loriban", "Bane alguém de usar a Loritta") {
            executor = LoriBanExecutor
        }

        subcommand("loriunban", "Bane alguém de usar a Loritta") {
            executor = LoriUnbanExecutor
        }
    }
}