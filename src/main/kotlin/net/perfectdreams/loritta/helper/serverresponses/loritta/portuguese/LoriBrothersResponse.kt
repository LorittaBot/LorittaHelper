package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * No, Loritta doesn't have any brothers or sisters, and she don't will have one until
 * we feel that we need one more bot.
 */
class LoriBrothersResponse : RegExResponse() {
    override val priority: Int
        get() = -2

    init {
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(irmã|irma)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                    LorittaReply(
                        "Não, eu não tenho nenhum irmão ou irmã, sou filha única e sou feliz assim! Não preciso ficar divindo minhas coisas com outras pessoas.",
                                Emotes.LORI_OWO
                    )
            )
}
