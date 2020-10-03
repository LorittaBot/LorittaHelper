package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

class LoriXpResponse : RegExResponse() {
    init {
        patterns.add("ganh(a|o)|sobe|subi|pega|pego|dá|vejo|ver".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(experi(ê|e)ncia|xp)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                LorittaReply(
                    "Você pode ver como a Loritta calcula a sua experiência nesta mensagem: https://discord.com/channels/420626099257475072/761337893951635458/761575827343147019",
                    prefix = "<a:lori_yay_wobbly:638040459721310238>"
                ),
                LorittaReply(
                    "Você pode ver quanta experiência você tem no servidor no `+perfil`",
                    mentionUser = false
                ),
                LorittaReply(
                    "Se você é da equipe do servidor, você também pode editar a experiência de usuários com `+editxp`!",
                    mentionUser = false
                )
            )
}