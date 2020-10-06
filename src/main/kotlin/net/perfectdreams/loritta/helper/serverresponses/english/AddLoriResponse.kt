package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Response when user asks how to add Loritta
 * to their guilds
 */
class AddLoriResponse : RegExResponse() {
    override val priority: Int
        get() = -998

    init {
        patterns.add("enable|put|use|add|call|invite".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Adding me to your server is easy! Just click here and add me to your server ^-^ <https://loritta.website/dashboard>",
                Emotes.LORI_PAC
            )
        )
}