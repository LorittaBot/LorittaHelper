package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people want to know how to enable
 * the join and leave messages
 */
class JoinLeaveResponse : RegExResponse() {
    init {
        patterns.add("enable|put|place|use|add|do|set".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("join|leave|welcome".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "**Enabling join/leave messages is very easy!**",
                    prefix = Emotes.LORI_PAC
                ),
                LorittaReply(
                    "Go to the admin dashboard here <https://loritta.website/dashboard> and pick the server you want to enable the messages on!",
                    mentionUser = false
                ),
                LorittaReply(
                    "Click \"Join/Leave Messages\"",
                    mentionUser = false
                ),
                LorittaReply(
                    "Now you just have to set it up the way you want it! <:eu_te_moido:366047906689581085>",
                    mentionUser = false
                ),
                LorittaReply(
                    "(Tip: If  you want to make one of those pretty message boxes, use our embed editor! <https://embeds.loritta.website/>)",
                    prefix = Emotes.LORI_OWO,
                    mentionUser = false
                )
            )
}