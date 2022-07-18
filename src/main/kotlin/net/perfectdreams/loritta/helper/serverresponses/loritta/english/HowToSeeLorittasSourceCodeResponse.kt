package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class HowToSeeLorittasSourceCodeResponse : RegExResponse()  {
        init {
            patterns.add("where|how|what".toPattern(Pattern.CASE_INSENSITIVE))
            patterns.add("see|is|read|get".toPattern(Pattern.CASE_INSENSITIVE))
            patterns.add("code|source|git".toPattern(Pattern.CASE_INSENSITIVE))
            patterns.add("lori|lorri|loritta|lorrita".toPattern(Pattern.CASE_INSENSITIVE))
        }
    
        override fun getResponse(message: String): List<LorittaReply> =
            listOf(
                    LorittaReply(
                            "You can find my source code here: https://bit.ly/lorittagit",
                            Emotes.LORI_PAT
                    )
            )
}