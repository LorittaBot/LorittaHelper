package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

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

    override fun getResponse(message: String) = listOf(
        LorittaReply(
            "O valor do resultado do `+ship` é baseado no nome das duas pessoas que você shippou. Ao alterar o seu nome no Discord, o valor do ship muda! O valor gerado é aleatório, e persiste até você trocar o nome novamente.",
            prefix = "❤️"
        ),
        LorittaReply(
            "Maaaaaas se você quiser alterar o valor sem ficar mudando o seu nome no Discord, você pode subornar o deus do amor no meu website! Ah, e não se esqueça que o suborno tem um custo de sonhos :3 <https://loritta.website/user/@me/dashboard/ship-effects>",
            mentionUser = false,
            prefix = Emotes.LORI_HEART
        ),
        LorittaReply(
            "Se por acaso no meio do processo o website retorne um erro, o famoso `Unknown user`, tente usar o ID do usuário no lugar da sua tag.",
            mentionUser = false,
            prefix = "<:lori_very_owo:562303822978875403>"
        ),
        LorittaReply(
            "Lembrando que ID e tag são coisas diferentes (`297153970613387264` é um **ID** e `Loritta#0219` é uma **tag**).",
            mentionUser = false,
            prefix = "<:lori_wow:626942886432473098>"
        ),
        LorittaReply(
            "Saiba como pegar o ID de uma conta aqui: https://discord.com/channels/420626099257475072/761337893951635458/761572663478321182",
            mentionUser = false,
            prefix = Emotes.LORI_COFFEE
        )
    )
}
