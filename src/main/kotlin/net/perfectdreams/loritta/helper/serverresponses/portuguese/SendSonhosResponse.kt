package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class SendSonhosResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("doar|paga|envia|envio|dar|dou|dá".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("sonhos".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Você pode enviar sonhos para uma pessoa utilizando `+pay @Usuário QuantidadeDeSonhos`",
                Emotes.LORI_PAC
            )
        )
}