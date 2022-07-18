package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Some people don't know what Pantufa's meaning,
 * and this response will explain it.
 */
class PantufaResponse : RegExResponse() {
    init {
        patterns.add("how|give|get|can|what".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("use|add|invite|do".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("pantufa|390927821997998081".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Pantufinha (<@390927821997998081>) is my best friend and the #1 helper in SparklyPower!",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "She does a lot of things related to my minecraft server, meaning... she might not be that interesting to you.",
                mentionUser = false
            ),
            LorittaReply(
                "(And she can blow your server up any time she wants!!) <:canella_triste:505191542982705174>",
                mentionUser = false
            )
        )
}