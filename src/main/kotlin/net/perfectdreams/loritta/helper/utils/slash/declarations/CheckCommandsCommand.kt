package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.CheckCommandsExecutor

class CheckCommandsCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "checkcommands",
        "Verifica quais comandos um usuário mais usa"
    ) {
        executor = CheckCommandsExecutor(helper)
    }
}