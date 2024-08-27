package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig

class CanIExchangeSonhosForSomethingElseResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "Se você não quer tomar riscos, é melhor ir pelo lado seguro e apenas trocar *objetos da Loritta por sonhos* e vice-versa. Seguindo isso você não terá nenhum problema e não será banido!"
            ),
            LorittaReply(
                "Existem algumas exceções, que você pode ler a seguir, mas saiba que qualquer troca que você fizer fora da Loritta é por sua conta e risco: https://discord.com/channels/420626099257475072/761337893951635458/801595165189079060",
                mentionUser = false
            )
        )
}
