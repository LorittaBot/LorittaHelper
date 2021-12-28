package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import dev.kord.common.entity.Snowflake
import io.ktor.client.request.*
import io.ktor.http.*
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.selects.SelectMenuExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.selects.SelectMenuWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.data.api.FanArtExistsResponse
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class SelectAttachmentSelectMenuExecutor(val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : SelectMenuWithDataExecutor {
    companion object : SelectMenuExecutorDeclaration(SelectAttachmentSelectMenuExecutor::class, "select_fa_attach")

    override suspend fun onSelect(user: User, context: ComponentContext, data: String, values: List<String>) {
        context.deferUpdateMessage()

        val data = ComponentDataUtils.decode<AddFanArtData>(data)
        val selectedAttachmentId = Snowflake(values.first().toLong())

        val message = m.helperRest.channel.getMessage(data.fanArtChannelId, data.fanArtMessageId)

        val attachment = message.attachments.first { it.id == selectedAttachmentId }

        val contentType = when (attachment.filename.substringAfterLast(".")) {
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

        val imageAsByteArray = LorittaHelperKord.http.get<ByteArray>(attachment.url)

        val checkResult = galleryOfDreamsClient.checkFanArt(
            imageAsByteArray,
            contentType,
        )

        if (checkResult is FanArtExistsResponse) {
            context.sendEphemeralMessage {
                content = "Já existe uma Fan Art com essa imagem! Talvez você esteja enviando uma Fan Art que já está na Galeria..."
            }
            return
        }

        context.updateMessage {
            apply(
                GalleryOfDreamsUtils.createMessage(
                    data.copy(
                        selectedAttachmentId = selectedAttachmentId
                    ),
                    message.attachments
                )
            )
        }
    }
}