package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Guide to mute and unmute members
 */
class MuteResponse : RegExResponse() {
    init {
        patterns.add("como|dá|sistema".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(silencia|muta|mute)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Para silenciar um usuário, basta usar `+mute <usuário> <motivo>`",
                prefix = "<:lori_pac:503600573741006863>"
            ),
            LorittaReply(
                "Logo em seguida eu irei perguntar o tempo do mute e basta você enviar no chat o tempo no qual você quer que o usuário fique mutado!",
                prefix = Emotes.LORI_BAN_HAMMER,
                mentionUser = false
            ),
            LorittaReply(
                "Ao silenciar, eu irei dar um cargo de `Silenciado` para o usuário!",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            ),
            LorittaReply(
                "Se você silenciou alguém sem querer ou se caso a pessoa se arrependa, para tirar o silenciamento de um usuário, basta usar `+unmute <usuário> <motivo>`",
                prefix = Emotes.LORI_COFFEE,
                mentionUser = false
            ),
            LorittaReply(
                "Se caso a pessoa ainda esteja conseguindo falar no chat, talvez alguma permissão do seu servidor ou canal esteja incorreta! Veja essa mensagem, ela também pode te ajudar com problemas no mute: https://discord.com/channels/420626099257475072/761337893951635458/761572286775033886",
                prefix = "<:lori_what:626942886361038868>",
                mentionUser = false
            )
        )
}
