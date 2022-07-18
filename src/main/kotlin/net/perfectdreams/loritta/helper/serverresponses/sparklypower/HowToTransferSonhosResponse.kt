package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class HowToTransferSonhosResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Transferir os seus sonhos é bem fácil!**",
                "<:pantufa_flushed:853048447212322856>"
            ),
            LorittaReply(
                "No canal de <#830658622383980545>, use `-lsx Fonte Destino Quantidade`.",
                mentionUser = false
            ),
            LorittaReply(
                "Um sonho da `loritta` equivalem a 2 sonecas no `survival`",
                mentionUser = false
            ),
            LorittaReply(
                "`-lsx survival loritta quantia` - do Sparkly para a Loritta",
                "<a:pantufa_lick:958906311414796348>",
                mentionUser = false
            ),
            LorittaReply(
                "`-lsx loritta survival quantia` - da Loritta para o Sparkly",
                "<a:pantufa_lick:958906311414796348>",
                mentionUser = false
            )
        )
}
