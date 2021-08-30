package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.DailyCheckExecutor

object DailyCheckCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "dailycheck",
        "Pega todos os dailies de vários usuários"
    ) {
        executor = DailyCheckExecutor
    }
}
