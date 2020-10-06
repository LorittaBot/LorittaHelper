package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Typing my name wrong is VERY usual, and this response reminds people about it.
 */
class LoriNameResponse: RegExResponse() {
    override val priority = -2000

    init {
        patterns.add("lorri|lorita".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(LorittaReply(
        message = "Just a reminder, my name is actually `Loritta` and my nickname is `Lori`, don't worry, it's very common to misspell my name. And yes, we can still be friends!",
        prefix = Emotes.LORI_OWO
    ))

}