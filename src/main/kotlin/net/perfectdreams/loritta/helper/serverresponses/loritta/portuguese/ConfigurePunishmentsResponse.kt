package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people ask about Loritta's
 * punishments system
 */
class ConfigurePunishmentsResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_PT.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("avisar|falar|enviar".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("punid|banid|kickad|expuls|mutad|silenciad".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Ativar as mensagens de punição é bem fácil!**",
                prefix = Emotes.LORI_COFFEE
            ),
            LorittaReply(
                "Vá no painel de administração clicando aqui <https://loritta.website/dashboard> e escolha o servidor que você deseja ativar as mensagens!",
                mentionUser = false
            ),
            LorittaReply(
                "Clique em \"Moderação\"",
                mentionUser = false
            ),
            LorittaReply(
                "Agora é só configurar do jeito que você queira! <:eu_te_moido:366047906689581085>",
                mentionUser = false
            ),
            LorittaReply(
                "(Dica: Você pode criar mensagens diferentes para cada tipo de punição na seção de \"Mensagens específicas para cada punição\"!)",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            )
        )
}