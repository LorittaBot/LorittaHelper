package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class HowToBuyPesadelosResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Você pode comprar pesadelos acessando o meu website! https://sparklypower.net/loja",
                "<:pantufa_coffee:853048446981111828>"
            ),
            LorittaReply(
                "Procurando algumas vantagens no SparklyPower?",
                mentionUser = false
            ),
            LorittaReply(
                "Querendo ajudar o SparklyPower a ficar online?",
                mentionUser = false
            ),
            LorittaReply(
                "Então você encontrou a sua solução!",
                "<a:pantufa_lick:958906311414796348>",
                mentionUser = false
            )
        )
}
