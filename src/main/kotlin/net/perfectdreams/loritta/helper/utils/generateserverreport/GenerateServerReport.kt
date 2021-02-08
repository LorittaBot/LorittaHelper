package net.perfectdreams.loritta.helper.utils.generateserverreport

import kotlinx.coroutines.future.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class GenerateServerReport(val m: LorittaHelper) {
    companion object {
        const val SERVER_REPORTS_CHANNEL_ID = 790308357713559582L
    }
    
    private val logger = KotlinLogging.logger {}

    val PRETTY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        .withLocale(Locale.US)
        .withZone(ZoneId.of("America/Sao_Paulo"))

    suspend fun onMessageReceived(event: GuildMessageReceivedEvent) {
        logger.info { "Received a report message!" }
        val attachment = event.message.attachments.first()

        val text = attachment.retrieveInputStream().await()
            .readAllBytes()
            .toString(Charsets.UTF_8)

        logger.info { "Attachment Text: $text" }

        val items = Json.decodeFromString(
            ListSerializer(GoogleFormItem.serializer()),
            text
        )

        // Time to parse the form responses
        val helperCode = items.first { it.question == "Coloque o código que a Loritta Helper te enviou no privado" }
            .answer.string.trim()

        logger.info { "Helper Code in the form response: $helperCode" }

        // Parse Helper Code
        val payload = try {
            Json.parseToJsonElement(
                EncryptionUtils.decryptMessage(m.config.secretKey, helperCode)
            ).jsonObject
        } catch (e: Exception) {
            logger.warn(e) { "Exception while decrypting code $helperCode" }
            return
        }

        val userId = payload["user"]!!.jsonPrimitive.long
        val time = payload["time"]!!.jsonPrimitive.long

        val userThatMadeTheReport = event.jda.retrieveUserById(userId).await()

        // Get the Report Type
        val reportType = items.first { it.question == "Qual é o motivo da denúncia?" }

        val communityGuild = event.jda.getGuildById(297732013006389252L) ?: return

        logger.info { "Report Type: ${reportType.answer.string}" }
        when (reportType.answer.string) {
            "Enviar convites não solicitados no privado/mensagem direta" -> {
                handleLoriInviteDMRules(event.jda, communityGuild, userThatMadeTheReport, reportType.answer.string, items)
            }

            "Divulgação não autorizada em servidores da Loritta/LorittaLand",
            "Ficar causando desordem no chat: Enviando a mesma mensagem várias vezes, enviando mensagens gigantes, etc",
            "Desrespeito a outros usuários (xingamentos, ofensas, toxicidade, etc) em servidores da Loritta/LorittaLand",
            "Enviar conteúdo NSFW em servidores da Loritta/LorittaLand" -> {
                handleBreakingLorittaLandRules(event.jda, communityGuild, userThatMadeTheReport, reportType.answer.string, items)
            }

            "Ofensas (Xingamentos) a Loritta" -> {
                handleLoriSwearingRules(event.jda, communityGuild, userThatMadeTheReport, reportType.answer.string, items)
            }

            "Comércio de Produtos com Valores Monetários por Sonhos (venda de Nitro por sonhos, vender sonhos por \$, etc)" -> {
                handleSonhosTradingRules(event.jda, communityGuild, userThatMadeTheReport, reportType.answer.string, items)
            }

            "Outros" -> {
                handleOtherRules(event.jda, communityGuild, userThatMadeTheReport, reportType.answer.string, items)
            }
        }

        // Send a message to the reporter, this helps them to be happy to know that we did receive their report
        userThatMadeTheReport.openPrivateChannel()
                .queue {
                    it.sendMessage("""Sua denúncia foi recebida com sucesso! <:lori_nice:726845783344939028>
                        |
                        |Quando a equipe decidir que a sua denúncia for válida e punir os meliantes de forma adequada, você irá receber uma mensagem falando que os meliantes foram punidos! <:lori_ok:731873534036541500>
                        | 
                        |Obrigada por denúnciar meliantes, suas denúncias ajudam bastante a equipe! <:smol_gessy:593907632784408644>
                    """.trimMargin())
                            .queue()
                }
    }

    private suspend fun handleLoriSwearingRules(
        jda: JDA,
        communityGuild: Guild,
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ) {
        val handleType = items.first { it.question == "Qual foi a gravidade da situação?" }
            .answer

        when (handleType.string) {
            "Ofendeu a Loritta em servidores da LorittaLand (servidor de suporte da Loritta, servidor de comunidade da Loritta, SparklyPower, etc)" -> {
                handleBreakingLorittaLandRules(
                    jda,
                    communityGuild,
                    userThatMadeTheReport,
                    "Ofensas a Loritta > Ofendeu a Loritta em servidores da LorittaLand",
                    items
                )
            }

            "Ofendeu a Loritta de coisas pesadas (racismo, homofobia, etc) em outros servidores" -> {
                handleLoriBrokeOtherServerRules(
                    jda,
                    communityGuild,
                    userThatMadeTheReport,
                    "Ofensas a Loritta > Ofendeu a Loritta de coisas pesadas (racismo, homofobia, etc) em outros servidores",
                    items
                )
            }
        }
    }

    private suspend fun handleSonhosTradingRules(
        jda: JDA,
        communityGuild: Guild,
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ) {
        val handleType = items.first { it.question == "Aonde é que o meliante fez isso?" }
            .answer

        when (handleType.string) {
            "Divulgou em servidores da LorittaLand (servidor de suporte da Loritta, servidor de comunidade da Loritta, SparklyPower, etc)" -> {
                handleBreakingLorittaLandRules(
                    jda,
                    communityGuild,
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou em servidores da LorittaLand",
                    items
                )
            }

            "Divulgou no meu privado/mensagem direta" -> {
                handleLoriInviteDMRules(
                    jda,
                    communityGuild,
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou no meu privado/mensagem direta",
                    items
                )
            }

            "Divulgou em outro servidor" -> {
                handleLoriBrokeOtherServerRules(
                    jda,
                    communityGuild,
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou em outro servidor",
                    items
                )
            }

            "Divulgou em outro servidor, e esse servidor é apenas para vendas de produtos por sonhos" -> {
                handleLoriBrokeOtherServerRules(
                    jda,
                    communityGuild,
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou em outro servidor, e esse servidor é apenas para vendas de produtos por sonhos",
                    items
                )
            }
        }
    }

    private suspend fun handleOtherRules(
        jda: JDA,
        communityGuild: Guild,
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ) {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val ruleBroken = items.first { it.question == "Qual regra ele quebrou?" }
            .answer.string

        val userId = items.first { it.question == "ID do Usuário" }
            .answer.string.toLongOrNull()

        val messageLinks = items.first { it.question == "Link da Mensagem" }
            .answer.string.replace("\n", " ").split(" ")

        val guildInvite = items.first { it.question == "Convite do Servidor" }
            .answer
            .string

        val images = items.first { it.question == "Imagem da Mensagem mostrando o que ela disse" }
            .answer
            .stringArray

        embed.addField(
            "Regra Quebrada",
            ruleBroken,
            false
        )

        embed.addField(
            "ID do Usuário",
            userId?.toString(),
            false
        )

        embed.addField(
            "Link da Mensagem",
            messageLinks.joinToString("\n"),
            false
        )

        embed.addField(
            "Convite do Servidor",
            guildInvite,
            false
        )

        embed.addField(
            "Imagens",
            images.joinToString("\n") { "https://drive.google.com/file/d/$it/view" },
            false
        )

        images.firstOrNull()?.let {
            embed.setImage("https://drive.google.com/uc?export=view&id=$it")
        }

        embed.addFinalConsiderations(items)

        communityGuild.getTextChannelById(SERVER_REPORTS_CHANNEL_ID)?.sendMessage(
            MessageBuilder()
                .setContent(
                    "<@&351473717194522647>"
                )
                .setEmbed(embed.build())
                .build()
        )
            ?.queue()
    }

    private suspend fun handleLoriInviteDMRules(
        jda: JDA,
        communityGuild: Guild,
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ) {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val userId = items.first { it.question == "ID do Usuário" }
            .answer.string.toLongOrNull()

        val messageLinks = items.first { it.question == "Link da Mensagem" }
            .answer.string.replace("\n", " ").split(" ")

        val guilds = items.first { it.question == "A pessoa está em servidores da LorittaLand? Se sim, quais?" }
            .answer
            .stringArray

        val images = items.first { it.question == "Imagem da Mensagem que te enviaram no privado" }
            .answer
            .stringArray

        embed.addField(
            "ID do Usuário",
            userId?.toString(),
            false
        )

        embed.addField(
            "Link da Mensagem",
            messageLinks.joinToString("\n"),
            false
        )

        embed.addField(
            "Servidores",
            guilds.joinToString("\n"),
            false
        )

        embed.addField(
            "Imagens",
            images.joinToString("\n") { "https://drive.google.com/file/d/$it/view" },
            false
        )

        images.firstOrNull()?.let {
            embed.setImage("https://drive.google.com/uc?export=view&id=$it")
        }

        embed.addFinalConsiderations(items)

        communityGuild.getTextChannelById(SERVER_REPORTS_CHANNEL_ID)?.sendMessage(
            MessageBuilder()
                .setContent(
                    "<@&351473717194522647>"
                )
                .setEmbed(embed.build())
                .build()
        )
            ?.queue()
    }

    private suspend fun handleLoriBrokeOtherServerRules(
        jda: JDA,
        communityGuild: Guild,
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ) {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val userId = items.first { it.question == "ID do Usuário" }
            .answer.string.toLongOrNull()

        val messageLinks = items.first { it.question == "Link da Mensagem" }
            .answer.string.replace("\n", " ").split(" ")

        val guildInvite = items.first { it.question == "Convite do Servidor" }
            .answer
            .string

        val images = items.first { it.question == "Imagem da Mensagem mostrando o que ela disse" }
            .answer
            .stringArray

        embed.addField(
            "ID do Usuário",
            userId?.toString(),
            false
        )

        embed.addField(
            "Link da Mensagem",
            messageLinks.joinToString("\n"),
            false
        )

        embed.addField(
            "Convite do Servidor",
            guildInvite,
            false
        )

        embed.addField(
            "Imagens",
            images.joinToString("\n") { "https://drive.google.com/file/d/$it/view" },
            false
        )

        images.firstOrNull()?.let {
            embed.setImage("https://drive.google.com/uc?export=view&id=$it")
        }

        embed.addFinalConsiderations(items)

        communityGuild.getTextChannelById(SERVER_REPORTS_CHANNEL_ID)?.sendMessage(
            MessageBuilder()
                .setContent(
                    "<@&351473717194522647>"
                )
                .setEmbed(embed.build())
                .build()
        )
            ?.queue()
    }

    private fun createBaseEmbed(userThatMadeTheReport: User, reportType: String) = EmbedBuilder()
        .setAuthor(
            userThatMadeTheReport.name + "#" + userThatMadeTheReport.discriminator + " (${userThatMadeTheReport.idLong})",
            null,
            userThatMadeTheReport.effectiveAvatarUrl
        )
        .setTitle("\uD83D\uDE93 $reportType")
        .setColor(reportType.hashCode() and 0x00FFFFFF)
        .setTimestamp(Instant.now())

    private suspend fun handleBreakingLorittaLandRules(
        jda: JDA,
        communityGuild: Guild,
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ) {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val messageLinks = items.first { it.question == "Link da Mensagem" }
            .answer.string.replace("\n", " ").split(" ")

        val savedMessages = StringBuilder()

        for ((index, link) in messageLinks.take(20).withIndex()) {
            val trueMessageIndex = index + 1
            val split = link.split("/")
            val messageId = split[split.size - 1].toLong()
            val channelId = split[split.size - 2].toLong()
            val guildId = split[split.size - 3].toLong()

            val guild = jda.getGuildById(guildId)
            val channel = jda.getTextChannelById(channelId)
            val message = try { channel?.retrieveMessageById(messageId)?.await() } catch (e: Exception) { null }

            if (channel != null && message != null) {
                embed.addField(
                    "\uD83E\uDDFE Mensagem #$trueMessageIndex",
                    """**Autor:** ${message.author.asMention}
                                    |**Servidor:** `${guild?.name}`
                                    |**Canal:** ${channel.asMention}
                                    |[Clique para ir na Mensagem](${message.jumpUrl})
                                """.trimMargin(),
                    false
                )

                val creationTime = message.timeCreated

                savedMessages.append("[${creationTime.format(PRETTY_DATE_FORMAT)}] (${message.author.idLong}) <Mensagem #$trueMessageIndex> ${message.author.name}#${message.author.discriminator}: ${message.contentRaw}")
                savedMessages.append("\n")
            } else {
                embed.addField(
                    "\uD83E\uDDFE Mensagem #$trueMessageIndex",
                    """A mensagem foi deletada, então eu não consigo descobrir quem enviou... <:lori_sob:556524143281963008>
                                    |**Servidor:** `${guild?.name}`
                                    |**Canal:** ${channel?.asMention}
                                    |[Link da Mensagem](${link})
                                """.trimMargin(),
                    false
                )
            }
        }

        if (savedMessages.isNotEmpty())
            embed.setFooter("As mensagens que eu consegui acessar foram salvas em um arquivo para te ajudar a banir o meliante \uD83D\uDE0A")

        embed.addFinalConsiderations(items)

        val query = communityGuild.getTextChannelById(SERVER_REPORTS_CHANNEL_ID)?.sendMessage(
            MessageBuilder()
                .setContent(
                    "<@&351473717194522647>"
                )
                .setEmbed(embed.build())
                .build()
        )

        if (savedMessages.isNotEmpty())
            query?.addFile(savedMessages.toString().toByteArray(Charsets.UTF_8), "messages.log")

        query?.queue()
    }

    private fun EmbedBuilder.addFinalConsiderations(items: List<GoogleFormItem>) {
        val finalConsiderations = items.firstOrNull { it.question == "Considerações finais" }
            ?.answer?.string

        if (!finalConsiderations.isNullOrBlank()) {
            val chunkedConsiderations = finalConsiderations.chunked(1000)

            if (chunkedConsiderations.size > 1) {
                for (chunk in chunkedConsiderations) {
                    addField("Considerações Finais", chunk, false)
                }
            } else {
                addField("Considerações Finais", finalConsiderations, false)
            }
        }
    }

    val JsonElement.string
        get() = this.jsonPrimitive.content
    val JsonElement.stringArray
        get() = this.jsonArray.map { it.string }

    @Serializable
    data class GoogleFormItem(
        val question: String,
        val answer: JsonElement
    )
}