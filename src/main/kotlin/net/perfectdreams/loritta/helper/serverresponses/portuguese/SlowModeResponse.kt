package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class SlowModeResponse : RegExResponse() {
    init {
        patterns.add("ativ|coloc|uso|adicio|add|boto|bota|coloca|adissiona".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(cool( )?down|del(a|e)y|slow( )?mode|modo( )?lento)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Você pode ativar slow mode/modo lento em um canal de texto com `+slowmode` ou nas configurações do canal no Discord!",
                Emotes.LORI_OWO
            )
        )
}