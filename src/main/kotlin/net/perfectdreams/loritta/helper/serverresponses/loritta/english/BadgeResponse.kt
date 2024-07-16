package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.AutomatedSupportResponse
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people ask about Loritta's badges
 * (not discord ones)
 */
class BadgeResponse : RegExResponse() {
    init {
        patterns.add("how".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("get|have|add".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("icon|icons|badg[e|es]".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getSupportResponse(message: String) =
        AutomatedSupportResponse(
            listOf(
                LorittaReply(
                    "Read more about badges and how you can acquire them in <#761337709720633392>",
                    Emotes.LORI_OWO
                )
            ),
            true
        )
}