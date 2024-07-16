package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.AutomatedSupportResponse
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class HowToVoteResponse : QuickAnswerResponse() {
    override fun getSupportResponse(message: String) =
        AutomatedSupportResponse(
            listOf(
                LorittaReply(
                    "Você pode votar no servidor acessando o meu website! https://sparklypower.net/votar",
                    "<:pantufa_coffee:853048446981111828>"
                ),
                LorittaReply(
                    "Você pode votar todos os dias para nos ajudar a crescer o SparklyPower cada vez mais!",
                    mentionUser = false
                ),
                LorittaReply(
                    "E, é claro, você vai receber alguns brindes em troca! 1 mapa, 1 diamante, 1 caixa secreta e 7 pesadelos.",
                    mentionUser = false
                )
            ),
            true
        )
}
