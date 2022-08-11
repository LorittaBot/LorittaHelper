package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.CheckCommandsExecutor

class CheckCommandsCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "checkcommands",
        "Verifica quais comandos um usuário mais usa"
    ) {
        executor = CheckCommandsExecutor(helper)
    }
}