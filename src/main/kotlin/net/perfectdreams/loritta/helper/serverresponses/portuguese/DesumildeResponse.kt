package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * We all know why we need this
 */
class DesumildeResponse: RegExResponse() {
    override val priority = -2000

    init {
        patterns.add("desumild".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> =
        listOf(
            LorittaReply(
                "https://cdn.discordapp.com/attachments/297732013006389252/773312894422482974/desumild.mp4"
            )
        )

}