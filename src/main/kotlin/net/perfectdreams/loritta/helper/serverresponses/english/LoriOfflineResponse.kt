package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Loritta can be down sometimes and everytime this happens, the support channel
 * turns into a complete chaos
 */
class LoriOfflineResponse : RegExResponse() {
    override val priority: Int
        get() = -999

    init {
        patterns.add("lori|loritta|297153970613387264".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(off|offline|down|maintenance)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                    LorittaReply(
                            "If Lori is offline, then check <#761385919479414825> to see if there's something wrong with her! Sometimes she just rebooted and will be back working shortly, just be pacient, and she should be back soon!",
                            Emotes.LORI_SOB
                    )
            )
}