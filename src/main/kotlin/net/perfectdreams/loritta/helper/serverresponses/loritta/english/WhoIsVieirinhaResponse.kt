package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to questions about Vieirinha, the nowadays oracle!
 */
class WhoIsVieirinhaResponse : RegExResponse() {
    init {
        patterns.add("who|what".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("vieirinha".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "A friend of mine who knows `@MrPowerGamerBR#4185` (my creator and dad :3) in real life, he's a good guy who likes to play Minecraft and enjoy life :3",
            Emotes.SUPER_VIEIRINHA
        )
    )
}
