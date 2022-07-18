package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know how to use the (+say) command
 */
class SayResponse : RegExResponse() {
    override val priority: Int
        get() = -998

    init {
        patterns.add(WHERE_IT_IS_EN.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(speak|say|send)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(message|text|word|something|string)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
            listOf(
                LorittaReply(
                    "You can *force* me to say something using `+say your message here`",
                    prefix = Emotes.LORI_PAC
                ),
                LorittaReply(
                    "You can also *pick* what server I'll send the message with `+say #channel-here your message here`",
                    mentionUser = false
                ),
                LorittaReply(
                    "But please, don't make me say mean things... If someone complains that I said something bad and I find out you did it, you'll be banned from using my features!",
                    Emotes.LORI_SOB,
                    mentionUser = false
                ),
                LorittaReply(
                    "(Tip: If you want to make one of those pretty square messages, use our embed editor! <https://embeds.loritta.website/>)",
                    prefix = Emotes.LORI_OWO,
                    mentionUser = false
                )
            )
}