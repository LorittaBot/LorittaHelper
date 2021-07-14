package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.AttachDenyReasonExecutor

object AttachDenyReasonCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "attachdenyreason",
        "Adiciona na mensagem da denúncia o motivo por qual ela foi negada"
    ) {
        executor = AttachDenyReasonExecutor
    }
}
