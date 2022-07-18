package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people don't know how to use commands
 */
class HowToUseCommandsResponse : RegExResponse() {
    override val priority = -999

    init {
        patterns.add(WHERE_IT_IS_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("command|cmd".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "You can use my commands using `+command`. My prefix is `+` by default, but in other servers it might be different!",
                Emotes.LORI_COFFEE
            ),
            LorittaReply(
                "To see what my prefix is in any server, send a message *just @pinging me* in the server's chat and I'll reply with the guild's prefix!",
            ),
            LorittaReply(
                "See all of my commands in my website! <https://loritta.website/commands>",
                Emotes.WUMPUS_KEYBOARD
            )
        )
}