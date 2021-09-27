package net.perfectdreams.loritta.helper.serverresponses.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to questions about ship customization
 * on Loritta's website
 */
class ValorShipResponse : RegExResponse() {
    init {
        patterns.add("change|alter|pay|bribe|set|rise|lower|make".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(ship)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "The `+ship` result value is based on the name of the two people you shipped. By changing your Discord name, the ship value will also change! The generated value is random, and persists until you change your name again.",
            prefix = "❤️"
        ),
        LorittaReply(
            "Buuuuuut if you want to change the value without having to change your Discord name, you can bribe the love god in my website! Oh, and don't forget that bribing costs Sonhos :3 <https://loritta.website/user/@me/dashboard/ship-effects>",
            mentionUser = false,
            prefix = Emotes.LORI_HEART
        )
    )
}