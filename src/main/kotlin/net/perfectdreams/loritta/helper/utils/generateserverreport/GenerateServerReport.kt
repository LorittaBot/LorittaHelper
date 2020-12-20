package net.perfectdreams.loritta.helper.utils.generateserverreport

import kotlinx.coroutines.future.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await

class GenerateServerReport(val m: LorittaHelper) {
    private val logger = KotlinLogging.logger {}

    suspend fun onMessageReceived(event: GuildMessageReceivedEvent) {
        val attachment = event.message.attachments.first()

        val text = attachment.retrieveInputStream().await()
                .readAllBytes()
                .toString(Charsets.UTF_8)

        val items = Json.decodeFromString(
                ListSerializer(GoogleFormItem.serializer()),
                text
        )

        // Time to parse the form responses
        val helperCode = items.first { it.answer == "Coloque o código que a Loritta Helper te enviou no privado" }
                .answer

        // Parse Helper Code

        // Get the Report Type
        val reportType = items.first { it.answer == "Qual é o motivo da denúncia?" }

        val communityGuild = event.jda.getGuildById(297732013006389252L) ?: return

        when (reportType.answer) {
            "Enviar convites não solicitados no privado/mensagem direta" -> TODO()
            "Divulgação não autorizada em servidores da Loritta/LorittaLand" -> {
                val embed = EmbedBuilder()
                        .setDescription(reportType.answer)

                val messageLinks = items.first { it.answer == "Link da Mensagem" }
                        .answer.replace("\n", " ").split(" ")

                for ((index, link) in messageLinks.take(20).withIndex()) {
                    val split = link.split("/")
                    val messageId = split[split.size - 1].toLong()
                    val channelId = split[split.size - 2].toLong()
                    val guildId = split[split.size - 3].toLong()

                    val guild = event.jda.getGuildById(guildId)
                    val channel = event.jda.getTextChannelById(channelId)
                    val message = channel?.retrieveMessageById(messageId)?.await()

                    embed.addField(
                            "Mensagem $index",
                            "Enviado por ${message?.author?.asMention}\n[Link](${message?.jumpUrl})",
                            true
                    )
                }

                communityGuild.getTextChannelById(790308357713559582L)?.sendMessage(embed.build())?.queue()
            }

            "Ficar causando desordem no chat: Enviando a mesma mensagem várias vezes, enviando mensagens gigantes, etc" -> TODO()
            "Desrespeito a outros usuários (xingamentos, ofensas, toxicidade, etc) em servidores da Loritta/LorittaLand" -> TODO()
            "Enviar conteúdo NSFW em servidores da Loritta/LorittaLand" -> TODO()
            "Ofensas (Xingamentos) a Loritta" -> TODO()
            "Comércio de Produtos com Valores Monetários por Sonhos (venda de Nitro por sonhos, vender sonhos por \$, etc)" -> TODO()
        }
    }

    @Serializable
    data class GoogleFormItem(
            val question: String,
            val answer: String
    )
}