package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern.CASE_INSENSITIVE

/**
 * Response when people ask how to do an announcement with
 * Loritta (usually using the private channel)
 */
class AnnouncementsResponse: RegExResponse() {

    init {
        patterns.add(ACTIVATE_OR_CHANGE_EN.toPattern(CASE_INSENSITIVE))
        patterns.add("announc".toPattern(CASE_INSENSITIVE))
    }

    override fun getResponse(message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "If you want to make an announcement using Loritta, you can use `+say` to do it!",
            prefix = Emotes.WUMPUS_KEYBOARD
        ), LorittaReply(
            message = "If you're looking for a way to advertise on people's DMs, then I can't help you, because that's considered `Spam` under Discord's Terms of Service!",
            prefix = Emotes.LORI_SOB
        )
    )
}