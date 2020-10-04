package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class AddLoriResponse : RegExResponse() {
    override val priority: Int
        get() = -998

    init {
        patterns.add("ativ|coloc|uso|adicio|add|boto|bota|coloca|adissiona|convid".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Me adicionar no seu servidor é fácil! Apenas clique aqui e me adicione no seu servidor ^-^ <https://loritta.website/dashboard>",
                Emotes.LORI_PAC
            )
        )
}