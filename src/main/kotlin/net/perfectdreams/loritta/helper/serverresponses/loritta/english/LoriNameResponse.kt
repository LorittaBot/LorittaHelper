package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Typing my name wrong is VERY usual, and this response reminds people about it.
 */
class LoriNameResponse: RegExResponse() {
    override val priority = -2000

    init {
        patterns.add("lorri|lorita".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String): List<LorittaReply> = listOf(LorittaReply(
        message = "Just a reminder, my name is actually `Loritta` and my nickname is `Lori`. Don't worry, it's very common to misspell my name. And yes, we can still be friends!",
        prefix = Emotes.LORI_OWO
    ))

}