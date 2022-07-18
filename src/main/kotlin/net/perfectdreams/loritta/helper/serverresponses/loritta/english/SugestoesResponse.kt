package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Replies to people wanting to know how to send suggestions to be implemented
 * on me
 */
class SugestoesResponse : RegExResponse() {
    init {
        patterns.add("how|where".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("suggest|suggestions".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "You can suggest new things in Loritta's community server! <#761625835043291146>",
            prefix = "⭐"
        )
    )
}