package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.ButtonRoleSenderExecutor

class ButtonRoleSenderCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "buttonrolesender",
        "Envia a mensagem de cargos no canal selecionado"
    ) {
        executor = ButtonRoleSenderExecutor(helper)
    }
}
