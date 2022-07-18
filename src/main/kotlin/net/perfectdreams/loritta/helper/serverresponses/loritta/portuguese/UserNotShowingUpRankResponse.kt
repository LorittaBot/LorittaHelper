package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response about when someone is not showing up in Loritta's local ranking
 */
class UserNotShowingUpRankResponse : RegExResponse() {
    init {
        patterns.add("eu|pessoa|usuário|user|player|membro".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("não".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("aparece|mostra".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("rank|top".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Se alguém não está aparecendo em algum ranking local do seu servidor, provavelmente ela deve ser saído do servidor e depois entrou quando a Loritta estava reiniciando ou instável."
            ),
            LorittaReply(
                "Para resolver o problema, peça para o usuário usar qualquer comando da Loritta (por exemplo: `+ping`) que aí a Loritta irá detectar que o usuário está no servidor e ele irá aparecer novamente nos comandos de ranking locais!",
                mentionUser = false
            )
        )
}