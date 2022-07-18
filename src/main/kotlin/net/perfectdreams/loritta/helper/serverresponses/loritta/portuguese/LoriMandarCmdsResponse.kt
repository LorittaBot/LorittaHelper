package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * People always complain that they can't use commands, so
 * this is a response that explain every step to know what's wrong
 */
class LoriMandarCmdsResponse : RegExResponse() {
    override val priority = -999

    init {
        patterns.add("enviando|mandando|mandar|responde".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("comando|cmd".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "**Vamos ir por partes...**",
                    prefix = Emotes.LORI_THINKING
                ),
                LorittaReply(
                    "O que acontece ao me mencionar no seu servidor? Escreva uma mensagem *apenas* me mencionando e veja o que aparece!",
                    mentionUser = false
                ),
                LorittaReply(
                    "Eu respondi? Legal! Agora leia o que eu falei lá e arrume o problema! <:lori_yum:414222275223617546> Normalmente é porque você bloqueou o canal de texto para eu não poder usar comandos, ou porque você tirou as permissões de um cargo poder usar comandos lá ou outro probleminha básico...",
                    mentionUser = false
                ),
                LorittaReply(
                    "Eu não respondi? Então veja se eu tenho permissão para ler e falar no canal de texto (se eu não apareço nos membros online, provavelmente eu não tenho permissão para ler o canal!)",
                    mentionUser = false
                ),
                LorittaReply(
                    "Eu não respondi mas aparece que eu estou digitando mas nunca envio nada? Então deu ruim!",
                    mentionUser = false
                ),
                LorittaReply(
                    "Caso você não tenha conseguido resolver o problema, então envie uma mensagem para alguém do suporte! \uD83D\uDE09",
                    mentionUser = false
                )
            )
}