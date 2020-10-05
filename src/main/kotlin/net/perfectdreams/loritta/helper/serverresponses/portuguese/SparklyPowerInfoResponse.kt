package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to people wanting to know about my minecraft's server, SparklyPower!
 */
class SparklyPowerInfoResponse: RegExResponse() {
    
    init {
        patterns.add("sparkly".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "Psiu! Se você precisa de ajuda com coisas relatadas ao **SparklyPower:tm:**, recomendo que procure por ajuda no servidor direcionado a ele: ${Constants.SPARKLY_POWER_INVITE_CODE}!",
            prefix = Emotes.WUMPUS_KEYBOARD
        )
    )
}