package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know about StarBoard system
 */
class StarboardResponse : RegExResponse() {
    init {
        patterns.add("use|enable|add|what|how".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("star( )?board".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "The starboard is a system for members to \\\"pin\\\" messages they think are cool/interesting/funny. Messages go automatically to the starboard when they achieve an X number of ⭐ in the message's reactions. You can configure the Starboard on my dashboard! <https://loritta.website/dashboard>",
            Emotes.LORI_OWO
        )
    )
}