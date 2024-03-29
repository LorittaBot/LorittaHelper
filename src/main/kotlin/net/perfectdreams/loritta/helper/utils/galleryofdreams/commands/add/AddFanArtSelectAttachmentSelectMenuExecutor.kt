package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutor
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutorDeclaration
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.data.api.FanArtExistsResponse
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.GalleryOfDreamsUtils
import java.util.*

class AddFanArtSelectAttachmentSelectMenuExecutor(val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : SelectMenuExecutor {
    companion object : SelectMenuExecutorDeclaration(AddFanArtSelectAttachmentSelectMenuExecutor::class, "select_fa_attach")

    override suspend fun onSelect(user: User, context: ComponentContext, values: List<String>) {
        context.deferUpdateMessage()

        val data = GalleryOfDreamsUtils.CACHED_DATA[UUID.fromString(context.data)] ?: error("Unknown Data")

        var selectedAttachmentId: Snowflake? = Snowflake(values.first().toLong())

        val message = m.helperRest.channel.getMessage(data.fanArtChannelId, data.fanArtMessageId)

        val attachment = message.attachments.first { it.id == selectedAttachmentId }

        val contentType = when (data.extensionOverride ?: attachment.filename.substringAfterLast(".")) {
            "png" -> ContentType.Image.PNG
            "jpeg", "jpg" -> ContentType.Image.JPEG
            "gif" -> ContentType.Image.GIF
            else -> {
                context.sendEphemeralMessage {
                    content = "Formato de imagem não suportado!"
                }
                return
            }
        }

        context.updateMessage {
            content = "Baixando Fan Art... <a:SCLOADING:715824432450633749>"
        }

        val imageAsByteArray = LorittaHelperKord.http.get(attachment.url)
            .body<ByteArray>()

        context.updateMessage {
            content = "Verificando se a Fan Art já existe... <a:SCLOADING:715824432450633749>"
        }

        val checkResult = galleryOfDreamsClient.checkFanArt(
            imageAsByteArray,
            contentType,
        )

        if (checkResult is FanArtExistsResponse) {
            context.sendEphemeralMessage {
                content = "Já existe uma Fan Art com essa imagem! Talvez você esteja enviando uma Fan Art que já está na Galeria..."
            }
            selectedAttachmentId = null
            // We don't return here because we want to update the dropdown too
        }

        context.updateMessage {
            apply(
                GalleryOfDreamsUtils.createAddFanArtMessage(
                    when (data) {
                        is AddFanArtToExistingArtistData -> data.copy(
                            selectedAttachmentId = selectedAttachmentId
                        )
                        is AddFanArtToNewArtistData -> data.copy(
                            selectedAttachmentId = selectedAttachmentId
                        )
                    },
                    message.attachments
                )
            )
        }
    }
}