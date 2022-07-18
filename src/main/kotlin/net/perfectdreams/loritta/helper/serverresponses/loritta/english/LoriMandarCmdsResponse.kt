package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * People always complain that they can't use commands, so
 * this is a response that explain every step to know what's wrong
 */
class LoriMandarCmdsResponse : RegExResponse() {
    override val priority = -999

    init {
        patterns.add("send|answer|respond|say".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("command|cmd".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "**Ok, let's see...**",
                    prefix = Emotes.LORI_THINKING
                ),
                LorittaReply(
                    "What happens when you @mention me in your server? Write a message **just** pinging me and see what happens!",
                    mentionUser = false
                ),
                LorittaReply(
                    "Did I answer? Cool! Now read what I said and solve the problem! <:lori_yum:414222275223617546> It's usually because you blocked the text channel so I can't use commands, or because you took a role's permission to use my commands away, among other dummy mistakes like that...",
                    mentionUser = false
                ),
                LorittaReply(
                    "I didn't reply? Then check if I have permission to read and send messages in the text channel (if I don't show up in the online members list, I probably can't read the channel!)",
                    mentionUser = false
                ),
                LorittaReply(
                    "I didn't reply, and it says that I'm typing without actually sending anything? Then something really broke!",
                    mentionUser = false
                ),
                LorittaReply(
                    "If you weren't able to solve the issue with these steps, then send a message to someone from the support team! \uD83D\uDE09",
                    mentionUser = false
                )
            )
}