package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Just a simple guide to enable the members counter in your
 * discord server
 */
class MemberCounterResponse : RegExResponse() {
    init {
        patterns.add("ativ|coloc|adicio|tem".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(contador|counter)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Ativar o contador de membros é bem fácil!**",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "Vá no painel de administração clicando aqui <https://loritta.website/dashboard> e escolha o servidor que você deseja ativar o contador de membros!",
                mentionUser = false
            ),
            LorittaReply(
                "Clique em \"Contador de Membros\"",
                mentionUser = false
            ),
            LorittaReply(
                "Procure o canal que você deseja ativar o contador e, na caixinha de texto, coloque \"{counter}\" e salve",
                mentionUser = false
            ),
            LorittaReply(
                "Agora é só esperar alguém entrar no seu servidor e ver a mágica acontecer!",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            )
        )
}