package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.PendingScarletExecutor

object PendingScarletCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "pendingscarlet",
        "Scarlet Police, on Ghetto Patrol \uD83D\uDC83")
    {
        executor = PendingScarletExecutor
    }
}