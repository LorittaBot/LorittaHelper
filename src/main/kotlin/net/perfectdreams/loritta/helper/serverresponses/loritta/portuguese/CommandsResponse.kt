package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

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
        patterns.add("como|onde|saber|cad(e|ê)|qual".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("tem|vejo|v(e|ê)|mostr|lista".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("com(m)?andos".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Veja todos os meus comandos no meu website! <https://loritta.website/commands>",
                Emotes.WUMPUS_KEYBOARD
            )
        )
}