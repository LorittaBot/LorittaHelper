package net.perfectdreams.loritta.helper.listeners

import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.generateserverreport.EncryptionUtils

class PrivateMessageListener(val m: LorittaHelper) : ListenerAdapter() {
    companion object {
        val VALID_APPEAL_TEXTS = listOf(
            "apelo",
            "appeal"
        )
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromType(ChannelType.PRIVATE))
            return

        if (VALID_APPEAL_TEXTS.any { event.message.contentRaw.equals(it, true) }) {
            val json = buildJsonObject {
                put("user", event.author.idLong)
                put("time", System.currentTimeMillis())
            }

            val encryptedInformation = EncryptionUtils.encryptMessage(m.config.secretKey, json.toString())

            event.channel.sendMessage(
                """**Então... você está afim de fazer um apelo de ban na Loritta? Então você veio ao lugar certo! <:lorota_jubinha:500766283965661184>**
                        |
                        |Para fazer o seu apelo, acesse o nosso formulário para preencher e enviar o seu apelo!
                        |
                        |**Ao abrir o formulário, o código de acesso será preenchido automaticamente mas, caso não seja preenchido, copie o código e coloque no formulário!** `$encryptedInformation`
                        |
                        |*Observação: Não envie o link do formulário e nem o seu código para outras pessoas! Esse formulário é único e especial apenas para você e, se você passar para outras pessoas, elas vão poder fazer apelos com o seu nome, o que poderá prejudicar as suas chances de ser desbanido!*
                        |
                        |https://docs.google.com/forms/d/e/1FAIpQLSfcXdMHeG1ybXw_CfqQ5eYH-XFpW0fdiu6kYEBuA_TgmuaEYg/viewform?usp=pp_url&entry.645865978=${encryptedInformation.encodeURLParameter()}
                    """.trimMargin()
            ).queue()
        }
    }
}