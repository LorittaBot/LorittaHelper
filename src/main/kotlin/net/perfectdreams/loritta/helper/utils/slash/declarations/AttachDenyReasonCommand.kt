package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.dv8tion.jda.api.JDA
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.AttachDenyReasonExecutor

class AttachDenyReasonCommand(val helper: LorittaHelperKord, val jda: JDA) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "attachdenyreason",
        "Adiciona na mensagem da denúncia o motivo por qual ela foi negada"
    ) {
        executor = AttachDenyReasonExecutor(helper, jda)
    }
}
