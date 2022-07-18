package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to questions about our exchange system between GarticBot's coins (garticos) and dreams (A.K.A sonhos)
 */
class TransferGarticosResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_PT.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("pass|transf|troc|funciona".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("gartic".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Você pode transferir garticos por sonhos utilizando `gb.garticos Sonhos Quantia` em qualquer servidor que possua o GarticBot!",
                Emotes.WUMPUS_KEYBOARD
            ),
            LorittaReply(
                "Para mais informações, entre no servidor do Gartic! Você pode encontrar o convite em <#761956906368892958>",
                Emotes.LORI_OWO
            )
        )
}