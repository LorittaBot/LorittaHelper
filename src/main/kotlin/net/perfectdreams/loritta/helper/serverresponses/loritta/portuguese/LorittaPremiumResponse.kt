package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class LorittaPremiumResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Você pode ver todas as vantagens premium que você pode ter e como compra-lás no meu website! Lembre-se que as vantagens são mensais, ou seja, elas duram um mês! https://loritta.website/br/donate",
                prefix = "<:lori_rica:593979718919913474>",
            )
        )
}
