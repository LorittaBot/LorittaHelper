package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * People always want to be one of my bodyguards...
 * But currently we're out of slots!
 */
class NoStaffSpotResponse : RegExResponse() {
    init {
        patterns.add("how|have|are there".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("be|become|get|slots|spots".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(body(-| )?guard|adm|mod|helper|staff|support|team)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String): List<LorittaReply> =
        listOf(
            LorittaReply(
                "Unfortunately, we don't have room for new administrators, https://cdn.discordapp.com/attachments/358774895850815488/703645649995825182/stream.mp4"
            )
        )
}