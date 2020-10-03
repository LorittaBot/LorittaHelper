package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

class SugestoesResponse : RegExResponse() {
    init {
        patterns.add("como|onde".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("sugest(ã|a)o|sugere".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) = listOf(
        LorittaReply(
            "Você pode sugerir novas coisas no nosso servidor de comunidade da Loritta! <#761625835043291146>",
            prefix = "⭐"
        )
    )
}