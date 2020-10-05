package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Voting on me results in rewards, that's why people always want to know how
 * to vote!
 */
class VotarResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("vota|voto".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(LORI_NAME.toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) = listOf(
        LorittaReply(
            "Obrigada por querer votar em mim! Votar me ajuda a crescer e te recompensa com sonhos! Para ver o link, use `+dbl`!",
            Emotes.LORI_OWO
        )
    )
}