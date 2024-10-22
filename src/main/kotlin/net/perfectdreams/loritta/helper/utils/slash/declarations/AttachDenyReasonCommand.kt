package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.AttachDenyReasonExecutor

class AttachDenyReasonCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "attachdenyreason",
        "Adiciona na mensagem da denúncia o motivo por qual ela foi negada"
    ) {
        executor = AttachDenyReasonExecutor(helper)
    }
}
