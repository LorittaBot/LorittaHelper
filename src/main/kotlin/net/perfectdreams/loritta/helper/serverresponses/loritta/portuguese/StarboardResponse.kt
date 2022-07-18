package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know about StarBoard system
 */
class StarboardResponse : RegExResponse() {
    init {
        patterns.add("ativ|usa|serve|o( )?que( )?(é|e)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("star( )?board".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "O starboard serve como um sistema para que membros do seu servidor possam \\\"fixar\\\" mensagens que eles acharam legais/interessantes/divertidas, a mensagem irá ir automaticamente para o starboard quando ela tiver X reações de estrelas ⭐ na mensagem! Você pode configurar no Starboard no meu painel! <https://loritta.website/dashboard>",
            Emotes.LORI_OWO
        )
    )
}