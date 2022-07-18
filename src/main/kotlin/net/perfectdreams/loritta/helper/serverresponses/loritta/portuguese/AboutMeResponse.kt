package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people ask about Loritta's
 * profile 'about me'
 */
class AboutMeResponse : RegExResponse() {
    override fun handleResponse(message: String): Boolean = patterns.all {
        it.matcher(message).find()
    } && !message.contains("falar", true)

    init {
        patterns.add("quero|onde|como".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("eu|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("((alter|mud|fa(ç|ss)|troc)(a|o|ar)|fa(s|z))  ?(p(ra|ara) (alter|mud)(a|o|ar)|)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("a|o|m(eu|inha)|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("descri(c|ç(a|ã)o)|biografia|sobre?.mim|texto|messagem".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("d(e|o|a)|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("m(eu|inha)|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("p(erfil|rofile)|lo(rri|ri|)(ta|tta)|".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Você pode alterar a mensagem que fica no seu perfil usando o comando `+sobremim`, para usar ele é muitooo simples!",
                prefix = Emotes.LORI_COFFEE
            ),
            LorittaReply(
                "Basta digitar `+sobremim <texto>` e pronto! O seu texto estará mudado e pronto para agradar os seus olhos!",
                mentionUser = false,
                prefix = Emotes.LORI_PAT
            ),
            LorittaReply(
                "Mas tome cuidado! Eu não gosto de usuários que ficam colocando bobagens como (NSFW e outras coisas) no Sobre mim, você pode ser punido se fizer isto!",
                mentionUser = false,
                prefix = Emotes.LORI_BAN_HAMMER
            )
        )
}
