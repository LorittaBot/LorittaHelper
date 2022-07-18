package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class HowToRegisterResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Se registrar no servidor é bem fácil!**",
                "<:pantufa_flushed:853048447212322856>"
            ),
            LorittaReply(
                "No canal de <#830658622383980545>, use `-registrar [nick do Minecraft]`, depois dentro do servidor SparklyPower Survival, use `/discord registrar` e pronto, sua conta estará associada ao seu Discord!",
                mentionUser = false
            ),
            LorittaReply(
                "**Tá, mas para que serve o registro?**",
                "<:pantufa_analise:853048446813470762>",
                mentionUser = false
            ),
            LorittaReply(
                "Para que você possa transferir seus sonhos da Loritta para o Servidor e vice-versa;",
                mentionUser = false
            ),
            LorittaReply(
                "Para que você possa falar nos canais de voz e enviar imagens nos chats;",
                mentionUser = false
            ),
            LorittaReply(
                "Para que possamos identificar você no Discord caso haja algum problema, como precisar resetar sua senha.",
                mentionUser = false
            )
        )
}
