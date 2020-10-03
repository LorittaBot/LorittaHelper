package net.perfectdreams.loritta.helper.utils.dontmentionstaff

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.utils.Constants

class PortugueseDontMentionStaff : DontMentionStaff() {
    override val roleId = Constants.PORTUGUESE_LORITTA_SUPPORT_ROLE_ID
    override val channelId = Constants.PORTUGUESE_SUPPORT_CHANNEL_ID

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