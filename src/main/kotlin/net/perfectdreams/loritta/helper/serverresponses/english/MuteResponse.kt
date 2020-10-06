package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Guide to mute and unmute members
 */
class MuteResponse : RegExResponse() {
    init {
        patterns.add("how|give|system".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("mute|silence".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "To mute a user, just use `+mute`",
                prefix = "<:lori_pac:503600573741006863>"
            ),
            LorittaReply(
                "To remove a user's mute, just use `+unmute`",
                mentionUser = false
            ),
            LorittaReply(
                "When you mute someone, I'll give them the `Muted` role!",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            )
        )
}