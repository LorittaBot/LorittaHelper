package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

/**
 * Response when people want to change Loritta's language
 * on their discord guilds
 */
class LanguageResponse : RegExResponse() {
    init {
        patterns.add("change|set|put|choose|pick".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(language|portuguese|english|spanish)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                    LorittaReply(
                            "You can change my language using `+language` and picking the one that you want!",
                            "\uD83D\uDE09"
                    )
            )
}
