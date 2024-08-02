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
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveReportsOnReactionListener
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.awt.Color
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

class GenerateAppealsReport(val m: LorittaHelper) {
    val community = m.config.guilds.community
    val SERVER_APPEALS_CHANNEL_ID = community.channels.appeals

    private val logger = KotlinLogging.logger {}

    suspend fun onMessageReceived(event: MessageReceivedEvent) {
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

            val embed = createBaseEmbed(userThatMadeTheReport)

            val banReason = items.first { it.question == "Qual foi o motivo do seu banimento?" }
                .answer
                .string

            val whenWhereYouBanned = items.first { it.question == "Quando você foi banido?" }
                .answer
                .string

            val whyShouldYouBeUnbanned = items.first { it.question == "Porque você deveria ser desbanido?" }
                .answer
                .string

            val whyDidYouDoAWrongThing = items.first { it.question == "Porque o que você fez é errado?" }
                .answer
                .string

            val additionalComments = items.first { it.question == "Deseja comentar mais alguma coisa sobre o seu ban?" }
                .answer
                .string

            val howMuchYouAffectedLoritta = items.first { it.question == "Em uma escala de 1 a 10, o que você fez acabou afetando a Loritta e a equipe dela?" }
                .answer
                .string

            embed.apply {
                addField(
                    "Qual foi o motivo do seu banimento?",
                    banReason.toString().take(1_000),
                    false
                )

                addField(
                    "Quando você foi banido?",
                    whenWhereYouBanned,
                    false
                )

                addField(
                    "Porque você deveria ser desbanido?",
                    whyShouldYouBeUnbanned.take(1_000),
                    false
                )

                addField(
                    "Porque o que você fez é errado?",
                    whyDidYouDoAWrongThing.take(1_000),
                    false
                )

                addField(
                    "Deseja comentar mais alguma coisa sobre o seu ban?",
                    additionalComments.take(1_000),
                    false
                )

                addField(
                    "Em uma escala de 1 a 10, o que você fez acabou afetando a Loritta e a equipe dela?",
                    howMuchYouAffectedLoritta,
                    false
                )
            }

            val images = items.first { it.question == "Desenhe um Gatinho" }
                .answer
                .stringArray

            embed.addField(
                "Desenhe um Gatinho", images
                    .joinToString("\n") { "https://drive.google.com/file/d/$it/view" }, false
            )

            val imageUrl = run {
                val firstImage = images.firstOrNull() ?: return@run null
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

            val action = communityGuild.getTextChannelById(SERVER_APPEALS_CHANNEL_ID)?.sendMessage(
                MessageCreateBuilder()
                    .setContent("<@&351473717194522647>")
                    .setEmbeds(embed.build())
                    .build()
            )

            runCatching {
                if (imageUrl != null)
                    action?.addFiles(FileUpload.fromData(URL(imageUrl).openStream(), "image.png"))
            }.onFailure {
                logger.debug(it) { "Failed to attach image in message" }
            }

            action?.queueAndAddReactions()

            // Send a message to the appealer
            userThatMadeTheReport.openPrivateChannel()
                .queue {
                    it.sendMessage(
                        """Seu apelo foi recebida com sucesso! <:lori_nice:726845783344939028>
                        |
                        |Quando a equipe decidir que o seu apelo foi excelente e que você poderá ser desbanido, você irá receber uma mensagem avisando! <:lori_ok:731873534036541500>
                        | 
                        |Espero que o seu apelo tenha sido 100% honesto e sincero, respeitando todas as regras de apelos... se não, cabeças vão rolar. <:smol_gessy:593907632784408644>
                    """.trimMargin()
                    )
                        .queue()
                }
        } catch (e: Throwable) {
            logger.warn(e) { "Something went wrong while processing the report ${event.message.jumpUrl}!" }
            communityGuild.getTextChannelById(SERVER_APPEALS_CHANNEL_ID)?.sendMessage(
                MessageCreateBuilder()
                    .setContent(
                        "<@&351473717194522647> Alguma coisa deu errada ao processar o apelo da mensagem ${event.message.jumpUrl} feita por ${userThatMadeTheReport.asMention}... Tente verificar ela manualmente já que eu não fui boa o suficiente... <:lori_sob:556524143281963008>\n\n```\n${e.stackTraceToString()}\n```"
                    )
                    .build()
            )?.queue()

            // oof
            userThatMadeTheReport.openPrivateChannel()
                .queue {
                    it.sendMessage(
                        """Alguma coisa deu errada ao processar o seu apelo, por favor, mande o apelo manualmente para a equipe e diga que a Helper não foi boa o suficiente para processar o seu apelo... <a:sad_cat22:735468288762708038>
                    """.trimMargin()
                    )
                        .queue()
                }
        }
    }

    private fun createBaseEmbed(userThatMadeTheReport: User) = EmbedBuilder()
        .setAuthor(
            userThatMadeTheReport.name + "#" + userThatMadeTheReport.discriminator + " (${userThatMadeTheReport.idLong})",
            null,
            userThatMadeTheReport.effectiveAvatarUrl
        )
        .setTitle("\uD83D\uDE93 Apelo de Ban na Loritta")
        .setColor(Color.BLUE)
        .setTimestamp(Instant.now())

    private fun MessageCreateAction.queueAndAddReactions() {
        this.queue { message ->
            message.addReaction(ApproveReportsOnReactionListener.APPROVE_EMOTE).queue {
                message.addReaction(ApproveReportsOnReactionListener.REJECT_EMOTE).queue()
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

    class ReportMessage(
        val embed: EmbedBuilder,
        val messageFile: ByteArray? = null,
        val images: List<String>? = null
    )
}