package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Usually people look for the best ways to earn dreams (A.K.A sonhos),
 * and that's what this reply explains
 */
class ReceiveSonhosResponse : RegExResponse() {
    override val priority = -1

    init {
        patterns.add("(pra ?que|como)|(conseg|peg|ganh)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(us|serv)(e|o|a|ar)|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("sonhos".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                    LorittaReply(
                            "Opa! Parece que você tem uma dúvida sobre sonhos certo? **Mas você sabe o que são sonhos?** Sonhos é a moeda oficial do meu sistema de economia.",
                            prefix = "<:lori_what:626942886361038868>"
                    ),
                    LorittaReply(
                            "**Tá mas o que eu faço com sonhos?** Simples! Com sonhos você pode desde apostar com seu amigo, até mesmo embelezar seu perfil comprando backgrounds! Demais né?",
                            prefix = "<:lori_rica:593979718919913474>",
                            mentionUser = false
                    ),
                    LorittaReply(
                            "**Ok... Como eu consigo sonhos?** Você pode conseguir sonhos... dormindo!",
                            prefix = Emotes.LORI_PAC,
                            mentionUser = false
                    ),
                    LorittaReply(
                            "Brincadeirinha!! ^-^ Você pode pegar sonhos usando `+daily`, mas lembrando que, sonhos não são criptomoedas, então não saia por aí vendendo como se fosse.",
                            prefix = Emotes.LORI_OWO,
                            mentionUser = false
                    ),
                    LorittaReply(
                            "Se você quer saber outros jeitos de ganhar e gastar sonhos, veja a nossa mensagem no <#761337893951635458>, clique aqui para voar até ela: https://discord.com/channels/420626099257475072/761337893951635458/762003311892103189",
                            prefix = Emotes.LORI_COFFEE,
                            mentionUser = false
                    )
            )
}
