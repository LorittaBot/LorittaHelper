package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know how to send suggestions to be implemented
 * on me
 */
class SugestoesResponse : RegExResponse() {
    init {
        patterns.add("mo|onde|qual|existe|tem( )?jeito|ajuda|quer(o|ia)|tem algum ?(jeito|(ch|x)at)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("eu( )?posso|de|fa(ço|z|s(er|))|pra|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("fazer( uma)?|d(á|a)|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("suge(st(ã|a)o|re|rir)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "Você pode sugerir novas coisas no nosso servidor de comunidade da Loritta! <#761625835043291146>, no canal <#359139508681310212>!",
            mentionUser = false,
            prefix = "<:lori_wow:626942886432473098>"
        ),
        LorittaReply(
            "Antes de sugerir, verifique se a sua sugestão está no <#588449583781118009> (Canal de sugestões que serão ou não, implementadas na Lori).",
            mentionUser = false,
            prefix = "<a:lori_yay_wobbly:638040459721310238>"
        ),
        LorittaReply(
            "E não se esqueça de ler o tópico do canal para não fazer sugestões bobas, ou para não quebrar nenhuma regra no canal!",
            mentionUser = false,
            prefix = Emotes.LORI_BAN_HAMMER
        )
    )
}
