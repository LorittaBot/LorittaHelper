package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response to questions about the experience system
 * and how we count XP in messages
 */
class LoriXpResponse : RegExResponse() {
    init {
        patterns.add("win|up|get|give|see|calculate|calc|know|how many|show|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(experience|xp|level)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                LorittaReply(
                    "You can see how Loritta calculates XP on this message: https://discord.com/channels/420626099257475072/761337709720633392/762048853108981810",
                    prefix = "<a:lori_yay_wobbly:638040459721310238>"
                ),
                LorittaReply(
                    "You can see how much experience you have using `+profile`",
                    mentionUser = false
                ),
                LorittaReply(
                    "If you're apart of a server's staff, you can also edit a user's experience points using `+editxp`!",
                    mentionUser = false
                )
            )
}
