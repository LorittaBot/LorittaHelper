package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response that is sent when people want to know
 * how to do discord embeds system
 */
class EmbedsArbitraryResponse : RegExResponse() {
    init {
        patterns.add("make|get".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("message".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("like|pretty|box".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Well... I think you want to put your message in an `embed`, right? If so, use our embed editor! <https://embeds.loritta.website/>",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "You can use embeds in every message in the dashboard! Just replace the message content with the code on the embed editor page!",
                mentionUser = false
            ),
            LorittaReply(
                "You can also use the same code in other commands like `+say`!",
                mentionUser = false
            )
        )
}
