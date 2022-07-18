package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

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
        patterns.add("lori|297153970613387264".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(off|caiu|manuten(c|ç|ss)(a|ã)o)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                    LorittaReply(
                            "Se a Lori está offline, verifique o canal de <#752294116708319324> para ver se alguma coisa deu errada com ela! As vezes ela reiniciou e já irá voltar, só seja paciente que, daqui a pouco, já deve estar de volta!",
                            Emotes.LORI_SOB
                    )
            )
}
