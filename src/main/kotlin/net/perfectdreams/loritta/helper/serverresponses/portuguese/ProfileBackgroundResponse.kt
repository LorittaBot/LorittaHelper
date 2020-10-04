package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class ProfileBackgroundResponse : RegExResponse() {
    init {
        patterns.add(WHERE_IT_IS.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(ACTIVATE_OR_CHANGE.toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("banner|background|imagem|fundo|foto|papel".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("perfil|profile|\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "**Para mudar a imagem de fundo do seu perfil:** <https://loritta.website/user/@me/dashboard/profiles>",
                Emotes.LORI_PAC
            ),
            LorittaReply(
                "**Para mudar o design do seu perfil:** <https://loritta.website/user/@me/dashboard/backgrounds>",
                mentionUser = false
            ),
            LorittaReply(
                "**Para comprar novas imagens de fundo e designs de perfil:** <https://loritta.website/user/@me/dashboard/daily-shop>",
                    mentionUser = false
            )
        )
}