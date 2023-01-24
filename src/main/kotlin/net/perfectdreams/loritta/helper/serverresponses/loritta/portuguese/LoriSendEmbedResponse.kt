package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class LoriSendEmbedResponse : RegExResponse() {
    init {
        patterns.add("($LORI_NAME)?".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(como|quero) (faz(er)?)?".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(manda(r) )?embed(s)?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Você pode aprender tudo sobre embeds no nosso website: https://loritta.website/br/extras/faq-loritta/embeds",
                Emotes.LORI_SMART
            )
        )
}