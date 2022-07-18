package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class DailyCaptchaDoesNotWorkResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Leia: https://discord.com/channels/420626099257475072/761337893951635458/794624493011861525"
            )
        )
}
