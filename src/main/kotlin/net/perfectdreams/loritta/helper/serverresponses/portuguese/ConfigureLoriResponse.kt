package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class ConfigureLoriResponse : RegExResponse() {
    override val priority: Int
        get() = -999

    init {
        patterns.add("configu|painel".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("$LORI_NAME|painel".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Para você mexer nas configurações do seu servidor é só clicar aqui! <https://loritta.website/dashboard>",
                Emotes.LORI_OWO
            )
        )
}