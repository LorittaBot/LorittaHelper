package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to questions about our exchange system between GarticBot's coins (garticos) and dreams (A.K.A sonhos)
 */
class TransferGarticosResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("transfer|exchange|pass|get|work".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("garticos|gartic".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "You can exchange Garticos for Sonhos using `gb.garticos Sonhos Quantia` in any guild that has GarticBot!",
                Emotes.WUMPUS_KEYBOARD
            ),
            LorittaReply(
                "For more information, join Gartic's server! You can find the invite in <#761956906368892958>",
                Emotes.LORI_OWO
            )
        )
}
