package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response to questions about the experience system
 * and how we count XP in messages
 */
class LoriXpResponse : RegExResponse() {
    override val priority = -1

    init {
        patterns.add("ganh(a|o)|sobe|subi|pega|pego|dá|vejo|ver|saber|calcul|quanto|mostr".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(experi(ê|e)ncia|xp|n(í|i)vel)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "Você pode ver como a Loritta calcula a sua experiência no meu website! https://loritta.website/br/extras/faq-loritta/experience",
                    prefix = "<a:lori_yay_wobbly:638040459721310238>"
                ),
                LorittaReply(
                    "Você pode ver quanta experiência você tem no servidor no `+perfil`",
                    mentionUser = false
                ),
                LorittaReply(
                    "Se você é da equipe do servidor, você também pode editar a experiência de usuários com `+editxp`!",
                    mentionUser = false
                )
            )
}