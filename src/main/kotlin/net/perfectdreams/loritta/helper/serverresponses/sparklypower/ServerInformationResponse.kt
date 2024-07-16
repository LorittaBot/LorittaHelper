package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.AutomatedSupportResponse
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class ServerInformationResponse : QuickAnswerResponse() {
    override fun getSupportResponse(message: String) =
        AutomatedSupportResponse(
            listOf(
                LorittaReply(
                    "SparklyPower é o servidor de Minecraft Survival da Loritta & Pantufa! **IP:** `mc.sparklypower.net`",
                    "<:pantufa_hi:997662575779139615>"
                ),
                LorittaReply(
                    "**No computador:** Versões de *1.16* a *1.19*;",
                    "<a:wumpus_keyboard:682249824133054529>",
                    mentionUser = false
                ),
                LorittaReply(
                    "**No celular:** Versão *1.19.0* (sempre a versão mais recente disponível, sem ser beta), **porta:** (padrão)",
                    "<a:wumpus_keyboard:682249824133054529>",
                    mentionUser = false
                )
            ),
            true
        )
}
