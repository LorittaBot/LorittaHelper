package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class LostAccountResponse : RegExResponse() {
    init {
        patterns.add("perdi".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("minha".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("conta".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                LorittaReply(
                    "Nós não transferimos sonhos/casamentos/reputações/etc de contas que você perdeu o acesso. Nós não restauramos dados de contas suspensas pelo Discord por quebrarem os termos de uso.",
                    Emotes.LORI_SHRUG
                )
            )
}