package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know about StarBoard system
 */
class StarboardResponse : RegExResponse() {
    init {
        patterns.add("use|enable|add|what|how".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("star( )?board".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) = listOf(
        LorittaReply(
            "The starboard is a system for members to \\\"pin\\\" messages they think are cool/interesting/funny, the message will automatically go to the starboard when it has an X number of ⭐ in the message's reactions! You can configure the Starboard on my dashboard! <https://loritta.website/dashboard>",
            Emotes.LORI_OWO
        )
    )
}