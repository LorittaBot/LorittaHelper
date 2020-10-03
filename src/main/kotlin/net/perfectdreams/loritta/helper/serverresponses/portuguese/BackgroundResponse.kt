package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class BackgroundResponse : RegExResponse() {
    init {
        patterns.add("pega|pego|coloc|clc|faço|fasso|alter|boto|bota".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add(
            "(back( )?ground|(plano|foto|imagem)( )?(de|no|na)( )?fundo|(papel|foto|imagem)( )?(de|no|na)( )?parede)".toPattern(
                Pattern.CASE_INSENSITIVE
            )
        )
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