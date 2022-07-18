package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people ask how to change Loritta's
 * current prefix on their guilds
 */
class ChangePrefixResponse : RegExResponse() {
    override val priority = -1000

    init {
        patterns.add("troc|mud|alter|vem|padr(ã|a)o".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(prefixo|\\+)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Alterar o prefixo no seu servidor é fácil!**",
                prefix = "<:lori_pac:503600573741006863>"
            ),
            LorittaReply(
                "Vá no painel de administração clicando aqui <https://loritta.website/dashboard> e escolha o servidor que você deseja alterar o prefixo! (O meu prefixo padrão é `+`)",
                mentionUser = false
            ),
            LorittaReply(
                "Beeeem no topo da página, na seção de configurações gerais, você pode alterar o meu prefixo!",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            ),
            LorittaReply(
                "Você pode ver o prefixo no seu servidor enviando uma mensagem apenas me marcando no chat do seu servidor!",
                mentionUser = false
            )
        )
}