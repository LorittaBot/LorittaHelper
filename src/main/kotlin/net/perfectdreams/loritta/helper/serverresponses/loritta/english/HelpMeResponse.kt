package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import java.util.regex.Pattern

/**
 * Response when people don`t know how to solve
 * a problem and need help with anything, telling them to mention the support
 */
class HelpMeResponse(val config: LorittaHelperConfig) : RegExResponse() {
    override val priority: Int
        get() = -1000

    init {
        patterns.add("someone|help|nobody|no one".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("help|question|do|how|set".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            if (!message.contains(config.guilds.english.roles.englishSupport.toString())) {
                listOf(
                        LorittaReply(
                                "Pst! If you have a question, send it in the chat and ping the <@&${config.guilds.english.roles.englishSupport}> role!",
                                Emotes.LORI_PAT
                        )
                )
            } else listOf()
}