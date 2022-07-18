package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Loritta can be down sometimes and everytime this happens, the support channel
 * turns into a complete chaos
 */
class LoriOfflineResponse : RegExResponse() {
    override val priority: Int
        get() = -999

    init {
        patterns.add("lori|loritta|297153970613387264".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(off|offline|down|maintenance)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                    LorittaReply(
                            "If Lori is offline, then check <#761385919479414825> to see if there's something wrong with her! Sometimes she is just restarting and will be coming back shortly, just be patient!",
                            Emotes.LORI_SOB
                    )
            )
}