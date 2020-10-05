package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * Replies to questions about ship customization
 * on Loritta's website
 */
class ValorShipResponse : RegExResponse() {
    init {
        patterns.add("alter|muda|mudo|paga|pago|suborn|manipul|aument".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(ship)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) = listOf(
        LorittaReply(
            "O valor do resultado do `+ship` é baseado no nome das duas pessoas que você shippou. Ao alterar o seu nome no Discord, o valor do ship muda! O valor gerado é aleatório, e persiste até você trocar o nome novamente.",
            prefix = "❤️"
        ),
        LorittaReply(
            "Maaaaaas se você quiser alterar o valor sem ficar mudando o seu nome no Discord, você pode subornar o deus do amor no meu website! Ah, e não se esqueça que custa sonhos subornar :3 <https://loritta.website/user/@me/dashboard/ship-effects>",
            mentionUser = false,
            prefix = Emotes.LORI_HEART
        )
    )
}