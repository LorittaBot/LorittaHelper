package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.DailyCheckExecutor

object DailyCheckCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "dailycheck",
        "Pega todos os dailies de vários usuários"
    ) {
        executor = DailyCheckExecutor
    }
}
