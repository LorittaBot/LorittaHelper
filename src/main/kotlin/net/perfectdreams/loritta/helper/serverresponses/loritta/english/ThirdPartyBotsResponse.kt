package net.perfectdreams.loritta.helper.serverresponses.loritta.english

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import java.util.regex.Pattern

/**
 * We don't help people with third party bots, and some people don't understand that,
 * so to keep things clean, we recommend you to go to the bot's support server
 */
class ThirdPartyBotsResponse(val config: LorittaHelperConfig): RegExResponse() {
    private val english = config.guilds.english

    init {
        patterns.add("how|ẁhat|why|get|help|use|add".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("ayana|carl|carl-bot|dank( |-)memer|dyno|fredboat|garticbot|groovy|mee6|mantaro|droplet|rythm|tatsu|unbelievaboat|zero( |-)two|hydra".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "It looks like you want help with other bots, hm... We don't offer support for other bots here, if you need help, check if that bot's support server is listed in <#${english.channels.otherBots}> and ask for help there!",
            prefix = Emotes.LORI_COFFEE
        )
    )
}
