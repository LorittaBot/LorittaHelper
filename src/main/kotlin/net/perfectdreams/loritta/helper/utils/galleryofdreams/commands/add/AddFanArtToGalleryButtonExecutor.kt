package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add

import dev.kord.rest.json.request.DMCreateRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import net.perfectdreams.discordinteraktions.common.components.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.data.DiscordSocialConnection
import net.perfectdreams.galleryofdreams.common.data.api.CreateArtistWithFanArtRequest
import net.perfectdreams.galleryofdreams.common.data.api.FanArtExistsResponse
import net.perfectdreams.galleryofdreams.common.data.api.UploadFanArtRequest
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import java.util.*

class AddFanArtToGalleryButtonExecutor(val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(AddFanArtToGalleryButtonExecutor::class, "add_fa_gallery")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        val addFanArtData = ComponentDataUtils.decode<AddFanArtData>(data)

        if (addFanArtData.selectedAttachmentId == null) {
            context.sendEphemeralMessage {
                content = "Você esqueceu de selecionar uma Fan Art!"
            }
            return
        }

        context.deferUpdateMessage()

        val message = m.helperRest.channel.getMessage(addFanArtData.fanArtChannelId, addFanArtData.fanArtMessageId)

        val attachment = message.attachments.first { it.id == addFanArtData.selectedAttachmentId }

        val contentType = when (addFanArtData.extensionOverride ?: attachment.filename.substringAfterLast(".")) {
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
            return
        }

        context.updateMessage {
            content = "Enviando Fan Art... <a:SCLOADING:715824432450633749>"
        }

        val fanArtUrl = when (addFanArtData) {
            is AddFanArtToExistingArtistData -> {
                val result = galleryOfDreamsClient.uploadFanArt(
                    addFanArtData.artistId,
                    imageAsByteArray,
                    contentType,
                    UploadFanArtRequest(
                        UUID.randomUUID().toString(),
                        null,
                        null,
                        Instant.parse(message.timestamp),
                        addFanArtData.tags
                    )
                )

                "https://fanarts.perfectdreams.net/artists/${addFanArtData.artistSlug}/${result.fanArt.slug}"
            }

            is AddFanArtToNewArtistData -> {
                val result = galleryOfDreamsClient.createArtistWithFanArt(
                    imageAsByteArray,
                    contentType,
                    CreateArtistWithFanArtRequest(
                        addFanArtData.artistName,
                        addFanArtData.artistSlug,
                        listOf(DiscordSocialConnection(addFanArtData.artistDiscordId.value.toLong())),
                        UploadFanArtRequest(
                            UUID.randomUUID().toString(),
                            null,
                            null,
                            Instant.parse(message.timestamp),
                            addFanArtData.tags
                        )
                    )
                )

                "https://fanarts.perfectdreams.net/artists/${addFanArtData.artistSlug}/${result.fanArt.slug}"
            }
        }

        context.updateMessage {
            content = "Fan Art adicionada! <:gabriela_brush:727259143903248486> $fanArtUrl"

            // Remove action rows
            components = mutableListOf()
        }

        // Send that the fan art was successfully added to the user
        val dmChannel = m.helperRest.user.createDM(DMCreateRequest(addFanArtData.artistDiscordId))

        m.helperRest.channel.createMessage(
            dmChannel.id
        ) {
            content = """A sua fan art foi adicionada na minha galeria de fan arts, o lugar aonde o trabalho maravilhoso de nossos fãs é exposto! $fanArtUrl
                |
                |Obrigada por fazer fan arts! <:gabriela_brush:727259143903248486><:brush_heart:727272698849525761><:lori_brush:727259089905778799> https://cdn.discordapp.com/attachments/393332226881880074/957369574083407962/lori_lick.gif
            """.trimMargin()
        }
    }
}