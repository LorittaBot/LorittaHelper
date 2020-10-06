package net.perfectdreams.loritta.helper.serverresponses.english

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
                    message = "SparklyPower is Loritta & Pantufa Survival Minecraft Server! **IP:** `mc.sparklypower.net`",
                    prefix = Emotes.LORI_PAT
            ),
            LorittaReply(
                    message = "You can transfer your sonhos from Loritta to SparklyPower and vice-versa using `-lsx` in SparklyPower's Discord server!",
                    prefix = Emotes.LORI_COFFEE
            ),
            LorittaReply(
                    message = "If you need help with things related to **SparklyPower**, I recommend you look for it here: ${Constants.SPARKLY_POWER_INVITE_CODE}",
                    prefix = Emotes.WUMPUS_KEYBOARD
            )
    )
}