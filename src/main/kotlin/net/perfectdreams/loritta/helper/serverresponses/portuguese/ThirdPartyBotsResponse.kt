package net.perfectdreams.loritta.helper.serverresponses.portuguese

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
        patterns.add("como|oq|o que|oque|pq|por( |)qu(e|ê)|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("ayana|carl|carl-bot|dank( |-)memer|dyno|fredboat|garticbot|groovy|mee6|mantaro|droplet|rythm|tatsu|unbelievaboat|zero( |-)two|hydra".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "Aparentemente você precisa de ajuda com outros bots, hm... Não damos suporte para outros bots aqui, se você precisa de ajuda, confira se o servidor de suporte deste bot está listado em <#${OTHER_BOTS_CHANNEL_ID}> e peça ajuda por lá!",
            prefix = Emotes.LORI_COFFEE
        )
    )
}