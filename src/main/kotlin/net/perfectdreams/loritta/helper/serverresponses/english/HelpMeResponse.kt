package net.perfectdreams.loritta.helper.serverresponses.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people don`t know how to solve
 * a problem and need help with anything, telling them to mention the support
 */
class HelpMeResponse : RegExResponse() {
    override val priority: Int
        get() = -1000

    init {
        patterns.add("someone|help|nobody|no one".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("help|question|do|how|set".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            if (!message.contains(Constants.ENGLISH_LORITTA_SUPPORT_ROLE_ID.toString())) {
                listOf(
                        LorittaReply(
                                "Pst! If you have a question, write it in the chat and ping the <@&${Constants.ENGLISH_LORITTA_SUPPORT_ROLE_ID}> role!",
                                Emotes.LORI_PAT
                        )
                )
            } else listOf()
}