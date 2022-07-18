package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * If you lost your account, we do **NOT** transfer your data to your
 * new account.
 */
class LostAccountResponse : RegExResponse() {
    init {
        patterns.add("lost|locked out".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("my".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("account".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "We don't transfer sonhos/marriages/reputation/etc from accounts you lost access to. We also don't restore data from accounts suspended by Discord for breaking its terms of service.",
                    Emotes.LORI_SHRUG
                )
            )
}