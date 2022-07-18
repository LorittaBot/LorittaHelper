package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Usually people look for the best ways to earn dreams (A.K.A sonhos),
 * and that's what this reply explains
 */
class ReceiveSonhosResponse : RegExResponse() {
    override val priority = -1

    init {
        patterns.add("get|win".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("sonhos|dreams|currency|money".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                    LorittaReply(
                            "**You can get Sonhos... sleeping!**",
                            Emotes.LORI_PAC
                    ),
                    LorittaReply(
                            "Just kidding!! ^-^ You can get sonhos using `+daily`",
                            prefix = Emotes.LORI_OWO,
                            mentionUser = false
                    ),
                    LorittaReply(
                            "If you want to know other ways to get sonhos, read the <#761337709720633392> channel.",
                            prefix = Emotes.LORI_COFFEE,
                            mentionUser = false
                    )
            )
}