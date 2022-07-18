package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

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
        patterns.add("ativ|coloc|uso|adicio|add|boto|bota|coloca|adissiona|convid".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Me adicionar no seu servidor é fácil! Apenas clique aqui e me adicione no seu servidor ^-^ <https://loritta.website/dashboard>",
                Emotes.LORI_PAC
            )
        )
}