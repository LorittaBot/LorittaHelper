package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to questions about Vieirinha, the nowadays oracle!
 */
class WhoIsVieirinhaResponse : RegExResponse() {
    init {
        patterns.add("quem".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("vieirinha".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "Um amiguinho meu que conhece o `@MrPowerGamerBR#4185` (meu criador e o meu pai :3) na vida real, um parça bem gente boa que gosta de jogar Minecraft e viver a vida :3",
            Emotes.SUPER_VIEIRINHA
        )
    )
}