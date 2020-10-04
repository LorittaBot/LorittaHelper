package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class HowToUseCommandsResponse : RegExResponse() {
    override val priority = -999

    init {
        patterns.add(WHERE_IT_IS.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("comando|cmd".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Você pode usar meus comandos usando `+comando`, por padrão o meu prefixo é `+` mas em outros servidores o prefixo pode ser diferente!",
                Emotes.LORI_COFFEE
            ),
            LorittaReply(
                "Para ver o meu prefixo em qualquer servidor, envie uma mensagem *apenas me mencionando* no chat do seu servidor para ver qual é o meu prefixo!",
            ),
            LorittaReply(
                "Veja todos os meus comandos no meu website! <https://loritta.website/commands>",
                Emotes.WUMPUS_KEYBOARD
            )
        )
}