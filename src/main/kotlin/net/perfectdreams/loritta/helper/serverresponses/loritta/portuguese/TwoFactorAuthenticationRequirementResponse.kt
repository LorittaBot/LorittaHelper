package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response about when someone is not showing up in Loritta's local ranking
 */
class TwoFactorAuthenticationRequirementResponse : RegExResponse() {
    init {
        patterns.add("pede|precisa|problema|consigo".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("2fa|autenticação|celular|authy|google authenticator".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("daily|sonhos|prêmio".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Se a Loritta achar que a sua conta está suspeita, ela pode pedir para você ativar autenticação em duas etapas para poder pegar o daily, para poder diferenciar as pessoas que estão abusando das pessoas corretas e certas (como você!)"
            ),
            LorittaReply(
                "Para mais informações, veja: https://loritta.website/br/extras/faq-loritta/2fa",
                mentionUser = false
            )
        )
}