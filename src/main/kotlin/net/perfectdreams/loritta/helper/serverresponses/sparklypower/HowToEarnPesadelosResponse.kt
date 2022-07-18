package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class HowToEarnPesadelosResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Antes de tudo, o que são pesadelos?** Pesadelo é a moeda de _cash_ do SparklyPower, podendo ser usada para comprar VIP, blocos de proteção, e outras coisas que você pode saber mais usando o comando `/lojacash`.",
                "<:pantufa_shield:853057852402565180>"
            ),
            LorittaReply(
                "Além de comprar seu VIP e ter inúmeras vantagens, você ainda ajuda o servidor a ficar online, já que nada nesse mundo é de graça né.",
                "<:pantufa_ameno:854811058992447530>",
                mentionUser = false
            ),
            LorittaReply(
                "**Ok... e como consigo pesadelos?**",
                "<a:pantufa_pickaxe:997671670468853770>",
                mentionUser = false
            ),
            LorittaReply(
                "Ao votar no servidor, você ganhará 7 pesadelos, você pode votar 1x ao dia;",
                mentionUser = false
            ),
            LorittaReply(
                "Vencendo eventos - alguns eventos possuem uma % de chance de ganhar pesadelos e outros eventos já tem 100% de chance caso você ganhe;",
                mentionUser = false
            ),
            LorittaReply(
                "Apostando com `/rifa`, podendo ser visto em `/rifa cronograma`;",
                mentionUser = false
            ),
            LorittaReply(
                "Comprando com dinheiro real também é uma opção! Basta entrar no site (https://sparklypower.net/loja) colocar seu nick do Minecraft corretamente com maiúsculas e minúsculas, selecionar a forma de pagamento, pagar, esperar os dias úteis e pronto pesadelos comprados!",
                mentionUser = false
            )
        )
}
