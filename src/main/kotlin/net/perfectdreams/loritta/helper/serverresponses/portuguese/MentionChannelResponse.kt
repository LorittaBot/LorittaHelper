package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

class MentionChannelResponse : RegExResponse() {
    init {
        patterns.add("coloco|menciono|mencionar|mention".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("#|canal|channel".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Para você mencionar um canal de texto, escreva no chat `\\#nome-do-canal` (sim, com a barra!), envie a mensagem, copie o que irá aparecer no chat (algo assim `<#297732013006389252>`) e coloque na mensagem!",
                "\uD83D\uDE09"
            )
        )
}