package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.QuickAnswerResponse

class HowToResetPasswordResponse : QuickAnswerResponse() {
    override fun getResponse(message: String) =
        listOf(
            LorittaReply(
                "**Trocar a sua senha do servidor é bem fácil!**",
                "<:pantufa_lurk:849734159601238036>"
            ),
            LorittaReply(
                "Entre no servidor do Sparkly, no lobby, e utilize o comando `/changepass SenhaNova SenhaNova` (sim, são duas vezes);",
                mentionUser = false
            ),
            LorittaReply(
                "Lembre-se de estar atento quanto a sua senha! Caso esqueça e não esteja registrado com o Discord, terá que provar para a equipe que a conta é realmente sua e assim ser resetada.",
                mentionUser = false
            )
        )
}
