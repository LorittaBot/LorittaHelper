package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class HowToSeeLorittasSourceCodeResponse : RegExResponse()  {
        init {
            patterns.add("where|how".toPattern(Pattern.CASE_INSENSITIVE))
            patterns.add("see|is|read|get".toPattern(Pattern.CASE_INSENSITIVE))
            patterns.add("code|source|git".toPattern(Pattern.CASE_INSENSITIVE))
            patterns.add("lori|lorri|loritta|lorrita".toPattern(Pattern.CASE_INSENSITIVE))
        }
    
        override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> =
            listOf(
                    LorittaReply(
                            "You can read my source code here: https://bit.ly/lorittagit",
                            Emotes.LORI_PAT
                    )
            )
}