package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to questions about our exchange system between GarticBot's coins (garticos) and dreams (A.K.A sonhos)
 */
class TransferGarticosResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("transfer|exchange|pass|get|work".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("gartic".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "You can exchange Garticos for Sonhos using `gb.garticos Sonhos Quantia` in any server with GarticBot!",
                Emotes.WUMPUS_KEYBOARD
            ),
            LorittaReply(
                "For more information, join Gartic's server! You can find the invite in <#761956906368892958>",
                Emotes.LORI_OWO
            )
        )
}