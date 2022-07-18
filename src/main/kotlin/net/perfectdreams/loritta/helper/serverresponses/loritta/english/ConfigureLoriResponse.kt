package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people want to know how to configure Loritta
 * on their guilds
 */
class ConfigureLoriResponse : RegExResponse() {
    override val priority: Int
        get() = -999

    init {
        patterns.add("configu|panel|dashboard|setting".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("$LORI_NAME|panel|dashboard".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "To change your server settings, you just have to click here! <https://loritta.website/dashboard>",
                Emotes.LORI_OWO
            )
        )
}