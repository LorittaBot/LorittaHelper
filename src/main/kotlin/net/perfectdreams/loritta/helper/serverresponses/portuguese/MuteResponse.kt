package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class MuteResponse : RegExResponse() {
    init {
        patterns.add("como|dá|sistema".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(silencia|muta|mute)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Para silenciar um usuário, basta usar `+mute`",
                prefix = "<:lori_pac:503600573741006863>"
            ),
            LorittaReply(
                "Para tirar o silenciamento de um usuário, basta usar `+unmute`",
                mentionUser = false
            ),
            LorittaReply(
                "Ao silenciar, eu irei dar um cargo de `Silenciado` para o usuário!",
                prefix = Emotes.LORI_OWO,
                mentionUser = false
            )
        )
}