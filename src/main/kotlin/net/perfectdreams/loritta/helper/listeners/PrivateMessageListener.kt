package net.perfectdreams.loritta.helper.listeners

import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.generateserverreport.EncryptionUtils

class PrivateMessageListener(val m: LorittaHelper) : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        super.onPrivateMessageReceived(event)

        if (event.message.contentRaw.equals("denuncia", true) || event.message.contentRaw.equals("denúncia", true)) {
            val json = buildJsonObject {
                put("user", event.author.idLong)
                put("time", System.currentTimeMillis())
            }

            val encryptedInformation = EncryptionUtils.encryptMessage(m.config.secretKey, json.toString())

            event.channel.sendMessage(
                """**Então... você está afim de denunciar uns meliantes? Então você veio ao lugar certo! <:lorota_jubinha:500766283965661184>**
                        |
                        |Para fazer a sua denúncia, acesse o nosso formulário para preencher e enviar a sua denúncia!
                        |
                        |**Ao abrir o formulário, o código de acesso será preenchido automaticamente mas, caso não seja preenchido, copie o código e coloque no formulário!** `$encryptedInformation`
                        |
                        |*Observação: Não envie o link do formulário e nem o seu código para outras pessoas! Esse formulário é único e especial apenas para você e, se você passar para outras pessoas, elas vão poder fazer denúncias com o seu nome! Se você queria denunciar alguém de novo, envie \"denúncia\"!*
                        |
                        |https://docs.google.com/forms/d/e/1FAIpQLSe6NBwXkl2ZY9MpSfFcTO6gXEtDTTQSTX2pQouzamWV_5h5zw/viewform?usp=pp_url&entry.645865978=${encryptedInformation.encodeURLParameter()}
                    """.trimMargin()
            ).queue()
        }
    }
}