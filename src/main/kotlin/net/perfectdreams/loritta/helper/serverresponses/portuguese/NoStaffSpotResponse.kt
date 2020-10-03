package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

class NoStaffSpotResponse : RegExResponse() {
    init {
        patterns.add("como|tem".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("vaga|vira|ser".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(guarda(-| )?costas|adm|mod|ajudante|staff|suporte)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> =
        listOf(
            LorittaReply(
                "https://cdn.discordapp.com/attachments/358774895850815488/703645649995825182/stream.mp4"
            )
        )
}