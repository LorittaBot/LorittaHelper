package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people ask about Loritta's
 * punishments system
 */
class ConfigurePunishmentsResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("warn|speak|send|say".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("punish|banned|kicked|muted|silenced|punished".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Enabling punishment messages is very easy!**",
                prefix = Emotes.LORI_COFFEE
            ),
            LorittaReply(
                "Go to the admin dashboard here <https://loritta.website/dashboard> and pick the server you want to enable the messages on!",
                mentionUser = false
            ),
            LorittaReply(
                "Click \"Moderation\"",
                mentionUser = false
            ),
            LorittaReply(
                "Now you just have to set it up the way you want! <:eu_te_moido:366047906689581085>",
                mentionUser = false
            ),
            LorittaReply(
                "(Tip: You can make different messages for different kinds of punishments in the \"Specific messages for each punishment\" section!)",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            )
        )
}