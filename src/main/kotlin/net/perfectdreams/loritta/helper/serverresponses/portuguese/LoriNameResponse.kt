package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class LoriNameResponse: RegExResponse() {
    override val priority = -2000

    init {
        patterns.add("lorri|lorita".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(LorittaReply(
        message = "Apenas um lembrete, meu nome na verdade é `Loritta` e meu apelido é `Lori`, não se preocupe, errar meu nome é bem comum. E sim, ainda podemos ser amigos!",
        prefix = Emotes.LORI_OWO
    ))

}