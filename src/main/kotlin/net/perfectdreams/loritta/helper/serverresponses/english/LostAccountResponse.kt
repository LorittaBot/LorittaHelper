package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
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

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                LorittaReply(
                    "We don't transfer sonhos/marriages/reputation/etc from acconuts you lost access to. We also don't restore data from accounts suspended by Discord for breaking it's terms of service.",
                    Emotes.LORI_SHRUG
                )
            )
}