package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response when people want to change Loritta's language
 * on their discord guilds
 */
class LanguageResponse : RegExResponse() {
    init {
        patterns.add("troca|change|troco|altero|alterar".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(linguagem|língua|language|lingua|português|inglês|portugues|pt-br|pt-pt)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                    LorittaReply(
                            "Você pode alterar a minha linguagem usando `+language` e escolhendo a linguagem que você queira!",
                            "\uD83D\uDE09"
                    )
            )
}