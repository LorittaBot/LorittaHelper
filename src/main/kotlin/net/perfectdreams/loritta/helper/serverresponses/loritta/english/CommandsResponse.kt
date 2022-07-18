package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people want to read about
 * all Loritta's available commands
 */
class CommandsResponse : RegExResponse() {
    init {
        patterns.add("how|where|know|what|wich".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("has|see|show|list".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("com(m)?ands".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "You can see all my commands on my website! <https://loritta.website/commands>",
                Emotes.WUMPUS_KEYBOARD
            )
        )
}
