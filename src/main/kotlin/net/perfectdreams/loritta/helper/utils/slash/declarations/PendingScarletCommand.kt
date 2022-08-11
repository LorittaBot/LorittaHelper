package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.dv8tion.jda.api.JDA
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.PendingScarletExecutor

class PendingScarletCommand(val helper: LorittaHelperKord, val jda: JDA) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "pendingscarlet",
        "Scarlet Police, on Ghetto Patrol \uD83D\uDC83")
    {
        executor = PendingScarletExecutor(helper, jda)
    }
}