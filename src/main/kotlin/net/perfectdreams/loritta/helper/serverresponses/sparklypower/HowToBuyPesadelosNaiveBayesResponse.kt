package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.AutomatedSupportResponse

class HowToBuyPesadelosNaiveBayesResponse(sparklyNaiveBayes: SparklyNaiveBayes) : SparklyNaiveBayesResponse(SparklyNaiveBayes.QuestionCategory.BUY_PESADELOS, sparklyNaiveBayes) {
    override fun getSupportResponse(message: String): AutomatedSupportResponse {
        return AutomatedSupportResponse(
            listOf(
                LorittaReply(
                    "Você pode comprar pesadelos acessando o meu website! https://sparklypower.net/loja",
                    "<:pantufa_coffee:853048446981111828>"
                )
            ),
            false
        )
    }
}