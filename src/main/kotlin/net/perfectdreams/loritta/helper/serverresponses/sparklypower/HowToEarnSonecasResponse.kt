package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class HowToEarnSonecasResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Antes de tudo, o que são sonhos e sonecas?** Sonecas é a moeda oficial do sistema de economia do Sparkly e os sonhos é o da Loritta!",
                "<:pantufa_shield:853057852402565180>"
            ),
            LorittaReply(
                "**Ok... e como consigo sonecas?**",
                "<a:pantufa_pickaxe:997671670468853770>",
                mentionUser = false
            ),
            LorittaReply(
                "Usando o IP ou convite do servidor em seu status ou convite (`mc.sparklypower.net` ou `discord.gg/sparklypower`) após ter isso em seu status, use `-verificarstatus` para saber se está tudo certo e assim estará ganhando 15 sonecas por minuto. Os sonhos vão para o SparklyPower e não diretamente para a Loritta, você precisará de 24h de jogo acumuladas nos últimos 30 dias para transferir a quantia;",
                mentionUser = false
            ),
            LorittaReply(
                "Você também pode ganhar sonhos vendendo itens diversos como minérios ou farms, tanto em loja de players, como na `/warp loja`;",
                mentionUser = false
            ),
            LorittaReply(
                "Votando no Servidor, ganhando eventos, comprando raspadinhas com `/raspadinha` (como a da Loritta), apostando com `/rifa comprar` ou abrindo caixas secretas que podem ser adquiridas quebrando pedras na `/warp recursos`.",
                mentionUser = false
            )
        )
}
