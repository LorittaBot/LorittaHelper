package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.PendingScarletExecutor

class PendingScarletCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "pendingscarlet",
        "Scarlet Police, on Ghetto Patrol \uD83D\uDC83")
    {
        executor = PendingScarletExecutor(helper)
    }
}