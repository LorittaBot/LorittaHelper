package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.DailyCatcherCheckExecutor

object DailyCatcherCheckCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "dailycatchercheck",
    "Contas Fakes, temos que pegar!") {
        executor = DailyCatcherCheckExecutor
    }
}