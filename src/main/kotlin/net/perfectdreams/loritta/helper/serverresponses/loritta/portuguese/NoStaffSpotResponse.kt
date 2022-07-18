package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * People always want to be one of my bodyguards...
 * But currently we're out of slots!
 */
class NoStaffSpotResponse : RegExResponse() {
    init {
        patterns.add("como|tem".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("vaga|vira|ser".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(guarda(-| )?costas|adm|mod|ajudante|staff|suporte)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String): List<LorittaReply> =
        listOf(
            LorittaReply(
                "https://cdn.discordapp.com/attachments/358774895850815488/703645649995825182/stream.mp4"
            )
        )
}