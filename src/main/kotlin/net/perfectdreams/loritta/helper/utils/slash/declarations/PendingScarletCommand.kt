package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.PendingScarletExecutor

object PendingScarletCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "pendingscarlet",
        "Scarlet Police, on Ghetto Patrol \uD83D\uDC83")
    {
        executor = PendingScarletExecutor
    }
}