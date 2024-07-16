package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.AutomatedSupportResponse
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when user ask how to add emojis
 * on a message
 */
class AddEmotesOnMessageResponse : RegExResponse() {
    init {
        patterns.add("place|put|mention|add".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("emote|emoji".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("join|leave|youtube|twitch|loritta|lori".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getSupportResponse(message: String) =
        AutomatedSupportResponse(
            listOf(
                LorittaReply(
                    "To add an emoji, send `\\:emoji:` in the chat, copy what appears (something like `<:loritta:331179879582269451>`) and then paste it in the message!", Emotes.LORI_OWO
                )
            ),
            true
        )
}