package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response to questions about profile backgrounds
 */
class ProfileBackgroundResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(ACTIVATE_OR_CHANGE_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("banner|background|image|photo|paper|layout|design".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("profile|\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**To change your profile's background:** <https://loritta.website/user/@me/dashboard/backgrounds>",
                Emotes.LORI_PAC
            ),
            LorittaReply(
                "**To change your profile's design:** <https://loritta.website/user/@me/dashboard/profiles>",
                mentionUser = false
            ),
            LorittaReply(
                "**To buy new backgrounds designs for your profile:** <https://loritta.website/user/@me/dashboard/daily-shop>",
                    mentionUser = false
            )
        )
}
