package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Guide to mute and unmute members
 */
class MuteResponse : RegExResponse() {
    init {
        patterns.add("how|give|system".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("mute|silence".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "To mute a user, simply use `+mute`",
                prefix = "<:lori_pac:503600573741006863>"
            ),
            LorittaReply(
                "To unmute a user, again just simply use `+unmute`",
                mentionUser = false
            ),
            LorittaReply(
                "(Tip: When you mute someone, I'll give them the `Muted` role. Don't try deleting or messing with that role, because it can break things on my side!)",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            )
        )
}