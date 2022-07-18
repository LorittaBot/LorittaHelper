package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people ask about Loritta's badges
 * (not discord ones)
 */
class BadgeResponse : RegExResponse() {
    init {
        patterns.add("como".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("ter|cons[e|i]g[o|uir]".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("[í|i]con[e|es]|badg[e|s]|emblema|emblemas".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Veja mais sobre as badges e como você pode ganhá-las em <#761337893951635458>",
                Emotes.LORI_OWO
            )
        )
}