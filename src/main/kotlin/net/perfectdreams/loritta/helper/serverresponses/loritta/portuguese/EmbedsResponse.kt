package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response that is sent when people want to know
 * about discord embeds system
 */
class EmbedsResponse : RegExResponse() {
    init {
        patterns.add("ativ|coloc|uso|adicion|faç|fass".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(embed)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "Se você deseja criar uma embed, use o nosso editor de embeds! <https://embeds.loritta.website/>",
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