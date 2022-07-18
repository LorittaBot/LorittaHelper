package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class ReputationsResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Leia: https://canary.discord.com/channels/420626099257475072/761337893951635458/862332427510349824",
                prefix = "\uD83C\uDD99",
            )
        )
}
