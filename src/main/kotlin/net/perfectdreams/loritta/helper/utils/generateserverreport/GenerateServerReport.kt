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
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveReportsOnReactionListener
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.awt.Color
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

class GenerateServerReport(val m: LorittaHelper, val jda: JDA) {
    companion object {
        const val SERVER_REPORTS_CHANNEL_ID = 790308357713559582L
    }

    private val logger = KotlinLogging.logger {}

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
        val userThatMadeTheReport = event.jda.retrieveUserById(userId).await()
        val communityGuild = event.jda.getGuildById(297732013006389252L) ?: return

        try {
            // We don't check this yet
            val time = payload["time"]!!.jsonPrimitive.long

            // Get the Report Type
            val reportType = items.first { it.question == "Qual é o motivo da denúncia?" }

            logger.info { "Report Type: ${reportType.answer.string}" }

            val reportMessage = when (reportType.answer.string) {
                "Divulgação não autorizada em servidores da Loritta/LorittaLand",
                "Ficar causando desordem no chat: Enviando a mesma mensagem várias vezes, enviando mensagens gigantes, etc",
                "Desrespeito a outros usuários (xingamentos, ofensas, toxicidade, etc) em servidores da Loritta/LorittaLand",
                "Enviar conteúdo NSFW em servidores da Loritta/LorittaLand" ->
                    handleBreakingLorittaLandRules(
                        event.jda,
                        userThatMadeTheReport,
                        reportType.answer.string,
                        items
                    )
                "Enviar convites não solicitados no privado/mensagem direta" ->
                    handleLoriInviteDMRules(
                        userThatMadeTheReport,
                        reportType.answer.string,
                        items
                    )

                "Ofensas (Xingamentos) a Loritta" ->
                    handleLoriSwearingRules(
                        event.jda,
                        userThatMadeTheReport,
                        items
                    )

                "Comércio de Produtos com Valores Monetários por Sonhos (venda de Nitro por sonhos, vender sonhos por \$, etc)" ->
                    handleSonhosTradingRules(
                        event.jda,
                        userThatMadeTheReport,
                        items
                    )

                "Outros" ->
                    handleOtherRules(userThatMadeTheReport, reportType.answer.string, items)
                else -> null
            }

            if (reportMessage != null) {
                logger.info { "Sending report message..." }

                val images = reportMessage.images
                val embed = reportMessage.reportInfoEmbed

                if (images != null) {
                    embed.addField(
                        "Imagens", images
                            .joinToString("\n") { "https://drive.google.com/file/d/$it/view" }, false
                    )
                }

                val imageUrl = run {
                    val firstImage = images?.firstOrNull() ?: return@run null
                    return@run runCatching {
                        val urlString = "https://drive.google.com/uc?export=view&id=$firstImage"
                        val connection = URL(urlString)
                            .openConnection() as HttpURLConnection
                        connection.instanceFollowRedirects = false
                        connection.connect()
                        return@runCatching if (connection.responseCode == 302)
                            connection.getHeaderField("Location")
                        else
                            null
                    }.getOrNull()
                }

                if (imageUrl != null)
                    embed.setImage("attachment://image.png")

                val embeds = when (reportMessage) {
                    is DefaultReportMessage -> listOf(embed)
                    is ReportWithUserInfoMessage -> listOf(embed, reportMessage.userInfoEmbed)
                }

                val components = mutableListOf<Component>()
                if (reportMessage is ReportWithUserInfoMessage && reportMessage.reportedUserId != null) {
                    components.add(
                        Button.of(
                            ButtonStyle.SECONDARY,
                            // Hack because this ain't Discord InteraKTions (yet!)
                            "show_uid:${ComponentDataUtils.encode(
                                ShowUserIdData(reportMessage.reportedUserId)
                            )}",
                            "Mostrar ID",
                            Emoji.fromUnicode("\uD83D\uDCDD")
                        )
                    )
                }

                val action = communityGuild.getTextChannelById(SERVER_REPORTS_CHANNEL_ID)?.sendMessage(
                    MessageBuilder()
                        .setContent("<@&351473717194522647>")
                        .setEmbeds(*embeds.map { it.build() }.toTypedArray())
                        .also {
                            if (components.isNotEmpty())
                                it.setActionRows(ActionRow.of(components))
                        }
                        .build()
                )

                runCatching {
                    if (imageUrl != null)
                        action?.addFile(URL(imageUrl).openStream(), "image.png")
                }.onFailure {
                    logger.debug(it) { "Failed to attach image in message" }
                }

                if (reportMessage.messageFile != null)
                    action?.addFile(reportMessage.messageFile, "messages.log")

                action?.queueAndAddReactions()
            } else {
                throw IllegalStateException("reportMessage is null, not supposed to be null")
            }

            // Send a message to the reporter, this helps them to be happy to know that we did receive their report
            userThatMadeTheReport.openPrivateChannel()
                .queue {
                    it.sendMessage(
                        """Sua denúncia foi recebida com sucesso! <:lori_nice:726845783344939028>
                        |
                        |Quando a equipe decidir que a sua denúncia for válida e punir os meliantes de forma adequada, você irá receber uma mensagem falando que os meliantes foram punidos! <:lori_ok:731873534036541500>
                        | 
                        |Obrigada por denúnciar meliantes, suas denúncias ajudam bastante a equipe! <:smol_gessy:593907632784408644>
                    """.trimMargin()
                    )
                        .queue()
                }
        } catch (e: Throwable) {
            logger.warn(e) { "Something went wrong while processing the report ${event.message.jumpUrl}!" }
            communityGuild.getTextChannelById(SERVER_REPORTS_CHANNEL_ID)?.sendMessage(
                MessageBuilder()
                    .setContent(
                        "<@&351473717194522647> Alguma coisa deu errada ao processar a denúncia da mensagem ${event.message.jumpUrl} feita por ${userThatMadeTheReport.asMention}... Tente verificar ela manualmente já que eu não fui boa o suficiente... <:lori_sob:556524143281963008>\n\n```\n${e.stackTraceToString()}\n```"
                    )
                    .build()
            )?.queue()

            // oof
            userThatMadeTheReport.openPrivateChannel()
                .queue {
                    it.sendMessage(
                        """Alguma coisa deu errada ao processar a sua denúncia, por favor, mande a denúncia manualmente para a equipe e diga que a Helper não foi boa o suficiente para processar a sua denúncia... <a:sad_cat22:735468288762708038>
                    """.trimMargin()
                    )
                        .queue()
                }
        }
    }

    private suspend fun handleLoriSwearingRules(
        jda: JDA,
        userThatMadeTheReport: User,
        items: List<GoogleFormItem>
    ): ReportMessage? {
        val handleType = items.first { it.question == "Qual foi a gravidade da situação?" }
            .answer

        return when (handleType.string) {
            "Ofendeu a Loritta em servidores da LorittaLand (servidor de suporte da Loritta, servidor de comunidade da Loritta, SparklyPower, etc)" -> {
                handleBreakingLorittaLandRules(
                    jda,
                    userThatMadeTheReport,
                    "Ofensas a Loritta > Ofendeu a Loritta em servidores da LorittaLand",
                    items
                )
            }

            "Ofendeu a Loritta de coisas pesadas (racismo, homofobia, etc) em outros servidores" -> {
                handleLoriBrokeOtherServerRules(
                    userThatMadeTheReport,
                    "Ofensas a Loritta > Ofendeu a Loritta de coisas pesadas (racismo, homofobia, etc) em outros servidores",
                    items
                )
            }
            else -> null
        }
    }

    private suspend fun handleSonhosTradingRules(
        jda: JDA,
        userThatMadeTheReport: User,
        items: List<GoogleFormItem>
    ): ReportMessage? {
        val handleType = items.first { it.question == "Aonde é que o meliante fez isso?" }
            .answer

        return when (handleType.string) {
            "Divulgou em servidores da LorittaLand (servidor de suporte da Loritta, servidor de comunidade da Loritta, SparklyPower, etc)" -> {
                handleBreakingLorittaLandRules(
                    jda,
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou em servidores da LorittaLand",
                    items
                )
            }

            "Divulgou no meu privado/mensagem direta" -> {
                handleLoriInviteDMRules(
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou no meu privado/mensagem direta",
                    items
                )
            }

            "Divulgou em outro servidor" -> {
                handleLoriBrokeOtherServerRules(
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou em outro servidor",
                    items
                )
            }

            "Divulgou em outro servidor, e esse servidor é apenas para vendas de produtos por sonhos" -> {
                handleLoriBrokeOtherServerRules(
                    userThatMadeTheReport,
                    "Comércio de Sonhos > Divulgou em outro servidor, e esse servidor é apenas para vendas de produtos por sonhos",
                    items
                )
            }
            else -> null
        }
    }

    private suspend fun handleOtherRules(
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ): ReportMessage {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val ruleBroken = items.first { it.question == "Qual regra ele quebrou?" }
            .answer
            .string
            .trim()

        val userId = items.first { it.question == "ID do Usuário" }
            .answer
            .string
            .trim()
            .toLongOrNull()

        val messageLinks = items.first { it.question == "Link da Mensagem" }
            .answer.string.replace("\n", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim() }

        val guildInvite = items.first { it.question == "Convite do Servidor" }
            .answer
            .string
            .trim()

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

        embed.addFinalConsiderations(items)

        return ReportWithUserInfoMessage(
            embed,
            userId,
            createReportedUserEmbed(jda, userId),
            images = images
        )
    }

    private suspend fun handleLoriInviteDMRules(
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ): ReportMessage {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val userId = items.first { it.question == "ID do Usuário" }
            .answer
            .string
            .trim()
            .toLongOrNull()

        val messageLinks = items.first { it.question == "Link da Mensagem" }
            .answer.string.replace("\n", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim() }

        val guilds = items.first { it.question == "A pessoa está em servidores da LorittaLand? Se sim, quais?" }
            .answer
            .stringArray
            .map {
                it.trim()
            }

        val images = items.first { it.question == "Imagem da Mensagem que te enviaram no privado" }
            .answer
            .stringArray
            .map {
                it.trim()
            }

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

        embed.addFinalConsiderations(items)

        return ReportWithUserInfoMessage(
            embed,
            userId,
            createReportedUserEmbed(jda, userId),
            images = images
        )
    }

    private suspend fun handleLoriBrokeOtherServerRules(
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ): ReportMessage {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val userId = items.first { it.question == "ID do Usuário" }
            .answer
            .string
            .trim()
            .toLongOrNull()

        val messageLinks = items.first { it.question == "Link da Mensagem" }
            .answer.string.replace("\n", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim() }

        val guildInvite = items.first { it.question == "Convite do Servidor" }
            .answer
            .string
            .trim()

        val images = items.first { it.question == "Imagem da Mensagem mostrando o que ela disse" }
            .answer
            .stringArray
            .map { it.trim() }

        embed.apply {
            addField(
                "ID do Usuário",
                userId?.toString(),
                false
            )

            addField(
                "Link da Mensagem",
                messageLinks.joinToString("\n"),
                false
            )

            addField(
                "Convite do Servidor",
                guildInvite,
                false
            )

            addFinalConsiderations(items)
        }

        return ReportWithUserInfoMessage(
            embed,
            userId,
            createReportedUserEmbed(jda, userId),
            images = images
        )
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
        userThatMadeTheReport: User,
        reportType: String,
        items: List<GoogleFormItem>
    ): ReportMessage {
        val embed = createBaseEmbed(userThatMadeTheReport, reportType)

        val rawMessageLinks = items.first { it.question == "Link da Mensagem" }

        val messageLinks = rawMessageLinks
            .answer.string.replace("\n", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .filter { it.contains("discord") } // Avoids issues with users adding random dumb stuff to the message field
            .map { it.trim() }

        val savedMessages = StringBuilder()
        try {
            for ((index, link) in messageLinks.take(20).withIndex()) {
                val trueMessageIndex = index + 1
                val split = link.split("/")
                val messageId = split[split.size - 1].toLong()
                val channelId = split[split.size - 2].toLong()
                val guildId = split[split.size - 3].toLong()

                val guild = jda.getGuildById(guildId)
                val channel = jda.getTextChannelById(channelId)
                val message = try {
                    channel?.retrieveMessageById(messageId)?.await()
                } catch (e: Exception) {
                    null
                }

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

                    savedMessages.append("[${creationTime.format(Constants.PRETTY_DATE_FORMAT)}] (${message.author.idLong}) <Mensagem #$trueMessageIndex> ${message.author.name}#${message.author.discriminator}: ${message.contentRaw}")
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
        } catch (e: Exception) {
            logger.debug(e) { "Something gone wrong when trying to retrieve messages, maybe some dumb person sent a DM message link?" }

            embed.setDescription(
                """
                *(Eu não consegui verificar as mensagens, verifique você mesmo manualmente...)*
                
                ${rawMessageLinks.answer.string}
                """.trimIndent()
            )
        }

        if (savedMessages.isNotEmpty())
            embed.setFooter("As mensagens que eu consegui acessar foram salvas em um arquivo para te ajudar a banir o meliante \uD83D\uDE0A")

        embed.addFinalConsiderations(items)

        return if (savedMessages.isNotEmpty())
            DefaultReportMessage(embed, savedMessages.toString().toByteArray(Charsets.UTF_8))
        else
            DefaultReportMessage(embed)
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

    private fun MessageAction.queueAndAddReactions() {
        this.queue { message ->
            message.addReaction(ApproveReportsOnReactionListener.APPROVE_EMOTE).queue {
                message.addReaction(ApproveReportsOnReactionListener.REJECT_EMOTE).queue()
            }
        }
    }

    // ===[ AKIRA/GATO BOT ADDITIONS ]===
    private suspend fun createReportedUserEmbed(jda: JDA, userId: Long?): EmbedBuilder {
        if (userId == null)
            return EmbedBuilder()
                .setDescription(
                    buildString {
                        append("O ID do usuário denunciado é nulo! Essa sim é uma denúncia que vocês amam né? Só rejeitar falando que \"passa os dados direitos\"... Queria que o meu trabalho fosse fácil assim também...")
                    }
                )
                .setColor(Color(255, 94, 94))

        val user = try {
            jda.retrieveUserById(userId).await()
        } catch (e: ErrorResponseException) {
            return EmbedBuilder()
                .setDescription(
                    buildString {
                        append("O ID do usuário denunciado é inválido! Essa sim é uma denúncia que vocês amam né? Só rejeitar falando que \"passa os dados direitos\"... Queria que o meu trabalho fosse fácil assim também...")
                    }
                )
                .setColor(Color(255, 94, 94))
        }

        return EmbedBuilder()
            .setDescription(
                buildString {
                    append("**Usuário denunciado**: `${user.asTag}` (${user.idLong})\n")
                    append("**Data de Criação da Conta**: <t:${user.timeCreated.toEpochSecond()}:F>\n")
                    append("\n\n")
                    user.mutualGuilds.forEach {
                        append("✅ ${it.name}\n")
                    }
                }
            )
            .setThumbnail(user.effectiveAvatarUrl)
            .setColor(Color(154, 255, 94))
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

    sealed class ReportMessage(
        val reportInfoEmbed: EmbedBuilder,
        val messageFile: ByteArray? = null,
        val images: List<String>? = null
    )

    class DefaultReportMessage(
        reportInfoEmbed: EmbedBuilder,
        messageFile: ByteArray? = null,
        images: List<String>? = null
    ) : ReportMessage(reportInfoEmbed, messageFile, images)

    class ReportWithUserInfoMessage(
        reportInfoEmbed: EmbedBuilder,
        val reportedUserId: Long?,
        val userInfoEmbed: EmbedBuilder,
        messageFile: ByteArray? = null,
        images: List<String>? = null
    ) : ReportMessage(reportInfoEmbed, messageFile, images)
}