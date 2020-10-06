package net.perfectdreams.loritta.helper.serverresponses.english

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Constants.OTHER_BOTS_CHANNEL_ID
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * We don't help people with third party bots, and some people don't understand that,
 * so to keep things clean, we recommend you to go to the bot's support server
 */
class ThirdPartyBotsResponse: RegExResponse() {

    init {
        patterns.add("how|ẁhat|why|get|help|use|add".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("ayana|carl|carl-bot|dank( |-)memer|dyno|fredboat|garticbot|groovy|mee6|mantaro|droplet|rythm|tatsu|unbelievaboat|zero( |-)two|hydra".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "It looks like you want help with another bots, hm... We don't offer support for other bots here, if you need help, check if that bot's support server is listed in <#${OTHER_BOTS_CHANNEL_ID}> and ask for help there!",
            prefix = Emotes.LORI_COFFEE
        )
    )
}
