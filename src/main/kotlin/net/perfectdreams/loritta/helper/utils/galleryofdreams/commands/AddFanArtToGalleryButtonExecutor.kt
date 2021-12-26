package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import dev.kord.rest.json.request.DMCreateRequest
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.data.UploadFanArtRequest
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import java.util.*

class AddFanArtToGalleryButtonExecutor(val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(AddFanArtToGalleryButtonExecutor::class, "add_fa_gallery")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        val (artistId, artistSlug, fanArtChannelId, fanArtMessageId, selectedAttachmentId, tags) = ComponentDataUtils.decode<AddFanArtData>(data)

        if (selectedAttachmentId == null) {
            context.sendEphemeralMessage {
                content = "Você esqueceu de selecionar uma Fan Art!"
            }
            return
        }

        context.deferUpdateMessage()

        val message = m.helperRest.channel.getMessage(fanArtChannelId, fanArtMessageId)

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

        val result = galleryOfDreamsClient.uploadImage(
            artistId,
            imageAsByteArray,
            contentType,
            UploadFanArtRequest(
                UUID.randomUUID().toString(),
                null,
                null,
                Instant.parse(message.timestamp),
                tags
            )
        )

        val fanArtUrl = "https://fanarts.perfectdreams.net/artists/${artistSlug}/${result.fanArt.slug}"

        context.updateMessage {
            content = "Fan Art adicionada! <:gabriela_brush:727259143903248486> $fanArtUrl"
        }

        // Send that the fan art was successfully added to the user
        val dmChannel = m.helperRest.user.createDM(DMCreateRequest(message.author.id))

        m.helperRest.channel.createMessage(
            dmChannel.id
        ) {
            content = """A sua fan art foi adicionada na minha galeria de fan arts, o lugar aonde o trabalho maravilhoso de nossos fãs é exposto! $fanArtUrl
                |
                |Obrigada por fazer fan arts! <:gabriela_brush:727259143903248486><:brush_heart:727272698849525761><:lori_brush:727259089905778799> https://cdn.discordapp.com/attachments/513405772911345664/924776048242065469/cat_lick_sketchmichi_250.gif
            """.trimMargin()
        }
    }
}