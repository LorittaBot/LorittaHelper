package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response that is sent when people want to know
 * how to do discord embeds system
 */
class EmbedsArbitraryResponse : RegExResponse() {
    init {
        patterns.add("faç|fass|faz".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("mensage".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("assim|jeito|caixinha".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Bem... eu acho que você quer colocar uma mensagem em uma `embed`, certo? Se sim, use nosso editor de embeds! <https://embeds.loritta.website/>",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "Você pode usar embeds em qualquer mensagem do painel! Apenas substitua o conteúdo da mensagem pelo o código que está na página do editor!",
                mentionUser = false
            ),
            LorittaReply(
                "Você também pode usar os mesmos códigos no `+say`!",
                mentionUser = false
            )
        )
}