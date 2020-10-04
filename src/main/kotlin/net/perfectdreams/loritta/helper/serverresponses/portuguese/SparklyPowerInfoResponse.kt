package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class SparklyPowerInfoResponse: RegExResponse() {

    init {
        patterns.add("sparkly".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(
            LorittaReply(
                    message = "SparklyPower é o servidor de Minecraft Survival da Loritta & Pantufa! **IP:** `mc.sparklypower.net`",
                    prefix = Emotes.LORI_PAT
            ),
            LorittaReply(
                    message = "Você pode transferir seus sonhos da Loritta para o SparklyPower e vice-versa utilizando `-lsx` no servidor no Discord do SparklyPower",
                    prefix = Emotes.LORI_COFFEE
            ),
            LorittaReply(
                    message = "Se você precisa de ajuda com coisas relacionadas ao **SparklyPower**, recomendo que procure por ajuda aqui: ${Constants.SPARKLY_POWER_INVITE_CODE}",
                    prefix = Emotes.WUMPUS_KEYBOARD
            )
    )
}