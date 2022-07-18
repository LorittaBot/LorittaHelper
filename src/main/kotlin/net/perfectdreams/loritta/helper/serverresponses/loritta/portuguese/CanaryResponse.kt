package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people talk about Loritta Canary
 * (canary/experimental) version of Loritta
 */
class CanaryResponse : RegExResponse() {
    init {
        patterns.add("como|dá|da|posso|que|manda|quantos".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("usa|adiciona|convid|invit|faz|bota|boto|resgat|link|coloc".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("canary|beta|395935916952256523".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "A Loritta Canary (<@395935916952256523>) é a versão experimental (beta) da <@297153970613387264> (sim, eu tenho duas contas, não me julgue!)",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "Ou seja, as novidades vão primeiro para lá, testadas e depois vão para a minha conta principal! Nós fazemos assim para evitar colocar funções experimentais na minha conta principal.",
                mentionUser = false
            ),
            LorittaReply(
                "Ela é privada e você não pode adicionar ela, sorry! Se você quer utilizar alguma função que só tem na Canary, espere ela ser colocada na Loritta! E porque você iria querer uma versão minha que pode sem querer explodir o seu servidor? Deixe o seu servidor seguro e nunca adicione bots que você não sabe o que fazem!",
                mentionUser = false,
                prefix = Emotes.LORI_SOB
            )
        )
}
