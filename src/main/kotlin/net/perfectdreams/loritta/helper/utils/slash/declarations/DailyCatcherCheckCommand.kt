package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.DailyCatcherCheckExecutor

object DailyCatcherCheckCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "dailycatchercheck",
    "Contas Fakes, temos que pegar!") {
        executor = DailyCatcherCheckExecutor
    }
}