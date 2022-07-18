package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Typing my name wrong is VERY usual, and this response reminds people about it.
 */
class LoriNameResponse: RegExResponse() {
    override val priority = -2000

    init {
        patterns.add("lorri|lorita".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String): List<LorittaReply> = listOf(LorittaReply(
        message = "Apenas um lembrete, meu nome na verdade é `Loritta` e meu apelido é `Lori`, não se preocupe, errar meu nome é bem comum. E sim, ainda podemos ser amigos!",
        prefix = Emotes.LORI_OWO
    ))

}