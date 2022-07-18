package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response when people don't know how
 * to mention a channel
 */
class MentionChannelResponse : RegExResponse() {
    init {
        patterns.add("get|put|set|mention".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("#|channel|chat".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "To mention a chat, type `\\#channel-name` (yes, WITH the backslash!), send the message, see what shows up in the chat (something like `<#420628148044955648>`) and copy it into the message!",
                "\uD83D\uDE09"
            )
        )
}