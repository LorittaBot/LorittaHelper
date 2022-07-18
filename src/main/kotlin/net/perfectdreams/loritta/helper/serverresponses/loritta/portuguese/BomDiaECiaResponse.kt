package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response when people talk about Loritta Canary
 * (canary/experimental) version of Loritta
 */
class BomDiaECiaResponse : RegExResponse() {
    init {
        patterns.add(ACTIVATE_OR_CHANGE_PT.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("bom ?dia ?[e&] ?cia|b ?d ?& ?c".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Aaaaaaalô, você está me escutando? Você pode colocar o Bom Dia & Cia no seu servidor indo no painel de administração clicando aqui <https://loritta.website/dashboard>, escolha o servidor que você deseja ativar o Bom Dia & Cia, vá em \"+Miscêlanea\" e ative lá!",
                prefix = "<:yudi:446394608256024597>"
            ),
            LorittaReply(
                "O Bom Dia & Cia aparece aleatoriamente no seu servidor quando o seu chat é ativo, demorando entre 15 e 30 minutos desde a última aparição dele no seu chat!",
                mentionUser = false
            )
        )
}
