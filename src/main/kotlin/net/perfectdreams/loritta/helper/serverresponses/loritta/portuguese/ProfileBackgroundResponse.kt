package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response to questions about profile backgrounds
 */
class ProfileBackgroundResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_PT.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(ACTIVATE_OR_CHANGE_PT.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("banner|background|imagem|fundo|foto|papel|wall ?paper".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("perfil|profile|\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Para mudar a imagem de fundo do seu perfil:** <https://loritta.website/user/@me/dashboard/backgrounds>",
                Emotes.LORI_PAC
            ),
            LorittaReply(
                "**Para mudar o design do seu perfil:** <https://loritta.website/user/@me/dashboard/profiles>",
                mentionUser = false
            ),
            LorittaReply(
                "**Para comprar novas imagens de fundo e designs de perfil:** <https://loritta.website/user/@me/dashboard/daily-shop>",
                    mentionUser = false
            )
        )
}
