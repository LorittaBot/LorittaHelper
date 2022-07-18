package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * If you lost your account, we do **NOT** transfer your data to your
 * new account.
 */
class LostAccountResponse : RegExResponse() {
    init {
        patterns.add("perdi".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("minha".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("conta".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "Nós não transferimos sonhos/casamentos/reputações/etc de contas que você perdeu o acesso. Nós não restauramos dados de contas suspensas pelo Discord por quebrarem os termos de uso.",
                    Emotes.LORI_SHRUG
                )
            )
}