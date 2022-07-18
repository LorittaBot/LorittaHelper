package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when people talk about Loritta Canary
 * (canary/experimental) version of Loritta
 */
class CanaryResponse : RegExResponse() {
    init {
        patterns.add("how|can".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("use|add|invite|do|get|link".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("canary|beta|395935916952256523".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Loritta Canary (<@395935916952256523>) is the experimental (beta) version of <@297153970613387264> (yes, I have two accounts, don't judge me!)",
                prefix = Emotes.LORI_PAC
            ),
            LorittaReply(
                "That means new features are sent there first, get tested, and then finally come to my main account! This happens because we want to avoid getting experimental and unstable features breaking things in my main account.",
                mentionUser = false
            ),
            LorittaReply(
                "She's private so you can't add her, sorry! If you wish to use a canary-only feature, wait for it to make it's way to Loritta! And why would you want a version of mine that could accidentally blow up your server? Keep your server safe and never add bots you don't know what they do!",
                mentionUser = false,
                prefix = "<:canella_triste:505191542982705174>"
            )
        )
}