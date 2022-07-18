package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people ask how to change Loritta's
 * current prefix on their guilds
 */
class ChangePrefixResponse : RegExResponse() {
    override val priority = -1000

    init {
        patterns.add("change|pick|get|configure|default|set".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(prefix|\\+)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Changing my prefix on your server is easy!**",
                prefix = "<:lori_pac:503600573741006863>"
            ),
            LorittaReply(
                "Go to the admin dashboard here <https://loritta.website/dashboard> and pick the server you want to change the prefix on! (My default one is `+`)",
                mentionUser = false
            ),
            LorittaReply(
                "Right theeeere, in the top of the page, in the general settings section, you can change my prefix!",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            ),
            LorittaReply(
                "You can check what the prefix on your server is by sending a message just pinging me in your server's chat!",
                mentionUser = false
            )
        )
}