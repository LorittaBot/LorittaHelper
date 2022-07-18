package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know about the SlowMode system (+slowmode)
 */
class SlowModeResponse : RegExResponse() {
    init {
        patterns.add("add|get|put|use|set".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(cool( )?down|del(a|e)y|slow( )?mode|time)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "You can enable slow mode in a channel using `+slowmode`, or in the channel's discord configurations!",
                Emotes.LORI_OWO
            )
        )
}