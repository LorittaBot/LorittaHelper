package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.AttachDenyReasonExecutor

object AttachDenyReasonCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "attachdenyreason",
        "Adiciona na mensagem da denúncia o motivo por qual ela foi negada"
    ) {
        executor = AttachDenyReasonExecutor
    }
}
