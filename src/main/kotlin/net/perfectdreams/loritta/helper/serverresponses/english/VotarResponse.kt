package net.perfectdreams.loritta.helper.serverresponses.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Voting on me results in rewards, that's why people always want to know how
 * to vote!
 */
class VotarResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("vote|voting|discord bot list|dbl".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "Thanks for wanting to vote for me! It helps me grow and rewards you with Sonhos! To see the link, use `+dbl`!",
            Emotes.LORI_OWO
        )
    )
}