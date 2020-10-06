package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * People always want to be one of my bodyguards...
 * But currently we're out of slots!
 */
class NoStaffSpotResponse : RegExResponse() {
    init {
        patterns.add("how|have".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("be|become|get".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(body(-| )?guard|adm|mod|helper|staff|support|team)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> =
        listOf(
            LorittaReply(
                "unfortunly, we don't have room for new admins, https://cdn.discordapp.com/attachments/358774895850815488/703645649995825182/stream.mp4"
            )
        )
}