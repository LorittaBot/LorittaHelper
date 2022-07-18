package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * No, Loritta doesn't have any brothers or sisters, and she don't will have one until
 * we feel that we need one more bot.
 */
class LoriBrothersResponse : RegExResponse() {
    override val priority: Int
        get() = -2

    init {
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(brother|sister|bro|sis)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                    LorittaReply(
                        "No, I don't have a brother or sister, I'm an only child and I'm happy with that! This way I don't have to share my stuff with other people.",
                                Emotes.LORI_OWO
                    )
            )
}