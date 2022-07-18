package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know how to use the (+say) command
 */
class SayResponse : RegExResponse() {
    override val priority: Int
        get() = -998

    init {
        patterns.add(WHERE_IT_IS_PT.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(falar|say|enviar|mandar)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(mensagem|mensage|texto|palavra|algo)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "Você pode *forçar* que eu fale algo utilizando `+say Sua Mensagem Aqui`",
                    prefix = Emotes.LORI_PAC
                ),
                LorittaReply(
                    "Você também pode *escolher* qual canal eu irei mandar a mensagem com `+say #CanalAqui Sua Mensagem Aqui`",
                    mentionUser = false
                ),
                LorittaReply(
                    "Mas por favor, não fique fazendo eu falar coisas feias... Se alguém vir reclamar que eu falei algo feio e eu descobrir que foi você que fez, você será banido de usar a Loritta!",
                    Emotes.LORI_SOB,
                    mentionUser = false
                ),
                LorittaReply(
                    "(Dica: Se você quiser fazer aquelas mensagens bonitinhas quadradas, use o nosso editor de embeds! <https://embeds.loritta.website/>)",
                    prefix = Emotes.LORI_OWO,
                    mentionUser = false
                )
            )
}