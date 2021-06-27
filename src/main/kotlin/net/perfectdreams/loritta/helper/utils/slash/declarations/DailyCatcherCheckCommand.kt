package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.DailyCatcherCheckExecutor

object DailyCatcherCheckCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "dailycatchercheck",
    "Contas Fakes, temos que pegar!") {
        executor = DailyCatcherCheckExecutor
    }
}