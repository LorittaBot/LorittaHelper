package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people want to know about
 * Loritta's legacy music system (not currently available)
 */
class DJLorittaResponse : RegExResponse() {
    init {
        patterns.add(("$WHERE_IT_IS_EN|loritta").toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("get|play|add|place|listen|enable|put|config|set".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("music|song|dj".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Unfortunately, YouTube and Google made some changes and the music commands were removed... You can read more about it here <https://loritta.website/blog/youtube-google-block> (article in portuguese)",
                prefix = Emotes.LORI_SOB
            )
        )
}