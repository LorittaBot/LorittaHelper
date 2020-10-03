package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class ProfileBackgroundResponse : RegExResponse() {
    init {
        patterns.add("como".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("coloc[ar|a|o]|mud[a|o]|troc[a|o]|alter[a|o]".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("banner|background|imagem|fundo|foto".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("perfil|profile".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("\\?".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "**Para mudar a imagem de fundo do seu perfil:** <https://loritta.website/user/@me/dashboard/profiles>",
                Emotes.LORI_PAC
            ),
            LorittaReply(
                "**Para mudar o design do seu perfil:** <https://loritta.website/user/@me/dashboard/backgrounds>",
                Emotes.LORI_PAC
            ),
            LorittaReply(
                "**Para comprar novas imagens de fundo e designs de perfil:** <https://loritta.website/user/@me/dashboard/daily-shop>"
            )
        )
}