package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class LoriBrothersResponse : RegExResponse() {
    init {
        patterns.add("lori".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(irmã|irma)".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                    LorittaReply(
                        "Não, eu não tenho nenhum irmão ou irmã, sou filha única e sou feliz assim! Não preciso ficar divindo minhas coisas com outras pessoas.",
                                Emotes.LORI_OWO
                    )
            )
}