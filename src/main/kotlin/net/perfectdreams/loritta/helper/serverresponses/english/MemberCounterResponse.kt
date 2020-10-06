package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Just a simple guide to enable the members counter in your
 * discord server
 */
class MemberCounterResponse : RegExResponse() {
    init {
        patterns.add("enable|put|place|set|get|have|add".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(counter|number emojis)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "**Enabling the member counter is very easy!**",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "Go to the dashboard by clicking here <https://loritta.website/dashboard> and pick the server you want to enable the member counter on!",
                mentionUser = false
            ),
            LorittaReply(
                "Click \"Member Counter\"",
                mentionUser = false
            ),
            LorittaReply(
                "Look for the channel you want to put the counter on, and in the text box, write \"{counter}\" and save",
                mentionUser = false
            ),
            LorittaReply(
                "Now you just have to wait for someone to join the server to see the magic happen!",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            )
        )
}