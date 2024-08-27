package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

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
    private val english = config.guilds.english

    override val priority: Int
        get() = -1000

    init {
        patterns.add("algu?(e|é)?m|como|ninguém".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("ajud|d(ú|u)vida|help|faç|fass|coloco".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            if (!message.contains(english.roles.portugueseSupport.toString())) {
                listOf(
                        LorittaReply(
                                "Psiu! Se você está com uma dúvida, escreva a sua dúvida no chat e marque o cargo do <@&${english.roles.portugueseSupport}>!",
                                Emotes.LORI_PAT
                        )
                )
            } else listOf()
}