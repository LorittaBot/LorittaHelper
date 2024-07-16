package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply

class HowToBuyPesadelosNaiveBayesResponse(sparklyNaiveBayes: SparklyNaiveBayes) : SparklyNaiveBayesResponse(SparklyNaiveBayes.QuestionCategory.BUY_PESADELOS, sparklyNaiveBayes) {
    override fun getResponse(message: String): List<LorittaReply> {
        return listOf(
            LorittaReply(
                "Você pode comprar pesadelos acessando o meu website! https://sparklypower.net/loja",
                "<:pantufa_coffee:853048446981111828>"
            )
        )
    }
}