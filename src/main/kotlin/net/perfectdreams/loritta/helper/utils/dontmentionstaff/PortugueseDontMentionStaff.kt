package net.perfectdreams.loritta.helper.utils.dontmentionstaff

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper

class PortugueseDontMentionStaff : DontMentionStaff() {
    private val english = LorittaHelper.config.guilds.english

    override val roleId = english.roles.portugueseSupport
    override val channelId = english.channels.oldPortugueseSupport

    override fun getResponse() = listOf(
        LorittaReply(
            "**Não mencione pessoas da equipe!** As vezes elas podem estar ocupadas... vai se ela está cagando e você aí, incomodando ela...",
            prefix = "<:lori_rage:556525700425711636>"
        ),
        LorittaReply(
            "Se você precisa de ajuda, mencione o <@&$roleId> na mensagem da sua dúvida, obrigada!",
            mentionUser = false
        )
    )
}