package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class SocialNotificatorResponse : RegExResponse() {
    init {
        patterns.add("($LORI_NAME)?".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(n([aã])o) (notifica|avisa|posta) ((minha|meu)s? )?((tweet|video|live)s?)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Eu posso estar sem permissão de criar Webhooks no canal que você colocou pra notificar.",
                Emotes.LORI_THINKING
            ),
            LorittaReply(
                "Você pode ter chegado no limite de contas que é permitido no plano grátis (5 Contas).",
                Emotes.LORI_OWO,
                false
            ),
            LorittaReply(
                "Twitter/Twitch/Youtube pode estar limitando a quantidade de dados que eu tenho acesso, as vezes arruma com o tempo ou eu notifico com um pouco de delay.",
                Emotes.LORI_COFFEE,
                false
            ),
            LorittaReply(
                "Se caso não for as duas primeiras alternativas, mande o ID do servidor e o link que você está tentando notificar para minha equipe",
                Emotes.LORI_PAT,
                false
            )
        )
}