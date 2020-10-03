package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class AddEmotesOnMessageResponse : RegExResponse() {
    init {
        patterns.add("coloco|menciono|por|coloca".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("emote|emoji".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("entrada|saída|youtube|twitch|loritta".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Para você colocar um emoji, escreva no chat `\\:emoji:`, envie a mensagem, copie o que apareça (irá aparecer algo assim `<:loritta:331179879582269451>`) e coloque na mensagem!",
                Emotes.LORI_OWO
            )
        )
}