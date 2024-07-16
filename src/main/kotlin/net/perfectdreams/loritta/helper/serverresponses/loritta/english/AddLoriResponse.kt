package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.AutomatedSupportResponse
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when user asks how to add Loritta
 * to their guilds
 */
class AddLoriResponse : RegExResponse() {
    override val priority: Int
        get() = -998

    init {
        patterns.add("put|add|call|invite|get".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getSupportResponse(message: String) =
        AutomatedSupportResponse(
            listOf(
                LorittaReply(
                    "Adding me to your server is easy! Just click here and select the server you want to add me ^-^ <https://loritta.website/dashboard>",
                    Emotes.LORI_PAC
                )
            ),
            true
        )
}