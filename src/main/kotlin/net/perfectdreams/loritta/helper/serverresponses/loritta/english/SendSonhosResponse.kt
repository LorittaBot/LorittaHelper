package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know how to send sonhos to other people
 */
class SendSonhosResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("give|pay|send|donate".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("sonhos|money|dreams".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "You can send someone Sonhos using `+pay @user SonhosAmount`",
                Emotes.LORI_PAC
            )
        )
}