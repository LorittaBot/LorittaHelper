package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
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
        patterns.add("change|alter|pay|bribe|set|rise|lower".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(ship)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) = listOf(
        LorittaReply(
            "The `+ship` result value is based on the name of the two people who shipped. shippou. By changing your Discord name alterar o seu nome no Discord, the ship value will also change! The value generated is random, and persists until you change your name again.",
            prefix = "❤️"
        ),
        LorittaReply(
            "Buuuuuut if you want to change the value without having to change your Discord name, you can bribe the love god in my website! Oh, and don't forget that bribing costs Sonhos :3 <https://loritta.website/user/@me/dashboard/ship-effects>",
            mentionUser = false,
            prefix = Emotes.LORI_HEART
        )
    )
}