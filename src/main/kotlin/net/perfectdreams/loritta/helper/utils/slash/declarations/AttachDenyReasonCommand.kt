package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.AttachDenyReasonExecutor

object AttachDenyReasonCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "attachdenyreason",
        "Adiciona na mensagem da denúncia o motivo por qual ela foi negada"
    ) {
        executor = AttachDenyReasonExecutor
    }
}
