package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

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
        patterns.add("como|dá|da|posso|que".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("usa|adiciona|convid|invit|faz".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("pantufa|390927821997998081".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "A Pantufinha (<@390927821997998081>) é a minha melhor amiga e é a ajudante #1 do SparklyPower!",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "Ela faz coisas relacionadas com o meu servidor de Minecraft, ou seja... ela não é tão interessante para você.",
                mentionUser = false
            ),
            LorittaReply(
                "(E ela pode explodir o seu servidor a hora que ela quiser!!) <:canella_triste:505191542982705174>",
                mentionUser = false
            )
        )
}