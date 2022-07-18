package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know how to send sonhos to other people
 */
class SendSonhosResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_PT.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("doa|paga|envia|envio|dar|dou|dá".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("sonhos".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Você pode enviar sonhos para uma pessoa utilizando `+pay @Usuário QuantidadeDeSonhos`.",
                Emotes.LORI_PAC
            ),
            LorittaReply(
                "Não se esqueça, veja todas as regras da Loritta em https://loritta.website/guidelines, antes de fazer qualquer transação de sonhos! Você pode ser banido de utilizar a Lori se quebrar alguma dessas regras!",
                mentionUser = false,
                prefix = "<:lori_ban_hammer:741058240455901254>"
            ),
            LorittaReply(
                "Ao aceitar a transação, você não conseguirá pedir os sonhos de volta e a equipe não irá te ajudar a pegar eles de volta, então apenas envie sonhos para pessoas confiáveis!",
                mentionUser = false,
                prefix = "<:sad_cat_thumbs_up:686370257308483612>"
            )
        )
}
