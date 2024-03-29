package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import com.github.benmanes.caffeine.cache.Caffeine
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.builder.message.MessageBuilder
import net.perfectdreams.discordinteraktions.common.builder.message.actionRow
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.discordinteraktions.common.components.selectMenu
import net.perfectdreams.galleryofdreams.common.FanArtTag
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtData
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtSelectAttachmentSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtSelectBadgesSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGalleryButtonExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToNewArtistData
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtData
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtOnGalleryButtonExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtSelectBadgesSelectMenuExecutor
import java.util.*
import java.util.concurrent.TimeUnit

object GalleryOfDreamsUtils {
    val ALLOWED_ROLES = listOf(
        Snowflake(924649809103691786L)
    )
    val CACHED_DATA = Caffeine.newBuilder()
        .expireAfterAccess(1L, TimeUnit.HOURS)
        .build<UUID, AddFanArtData>()
        .asMap()

    fun createAddFanArtMessage(
        data: AddFanArtData,
        attachments: List<DiscordAttachment>
    ): MessageBuilder.() -> (Unit) = {
        val selectedAttachment = attachments.firstOrNull { it.id == data.selectedAttachmentId }
        val randomId = UUID.randomUUID()
        CACHED_DATA.getOrPut(randomId) { data }

        content = if (selectedAttachment != null) {
            if (data is AddFanArtToNewArtistData) {
                "**(Artista que não está no banco de dados da Galeria dos Sonhos! O artista será criado na galeria dos sonhos ao enviar a fan art)** Configure as informações da Fan Art! Selecionada: ${selectedAttachment.url}"
            } else {
                "Configure as informações da Fan Art! Selecionada: ${selectedAttachment.url}"
            }
        } else {
            if (data is AddFanArtToNewArtistData) {
                "**(Artista que não está no banco de dados da Galeria dos Sonhos! O artista será criado na galeria dos sonhos ao enviar a fan art)** Configure as informações da Fan Art!"
            } else {
                "Configure as informações da Fan Art!"
            }
        }

        actionRow {
            selectMenu(AddFanArtSelectAttachmentSelectMenuExecutor, randomId.toString()) {
                for (attachment in attachments) {
                    option(attachment.filename, attachment.id.toString()) {
                        if (attachment.id == data.selectedAttachmentId) {
                            default = true
                        }
                    }
                }
            }
        }

        actionRow {
            selectMenu(AddFanArtSelectBadgesSelectMenuExecutor, randomId.toString()) {
                this.allowedValues = 0..FanArtTag.values().size

                for (tag in FanArtTag.values()) {
                    option(tag.name, tag.ordinal.toString()) {
                        if (tag in data.tags) {
                            default = true
                        }
                    }
                }
            }
        }

        actionRow {
            if (data.selectedAttachmentId != null) {
                interactiveButton(
                    ButtonStyle.Success,
                    "Adicionar",
                    AddFanArtToGalleryButtonExecutor,
                    randomId.toString()
                )
            } else {
                interactionButton(
                    ButtonStyle.Success,
                    "disabled_button_plz_ignore"
                ) {
                    label = "Adicionar"
                    disabled = true
                }
            }
        }
    }

    fun createPatchFanArtMessage(
        data: PatchFanArtData
    ): MessageBuilder.() -> (Unit) = {
        content = "Configure as informações da Fan Art!"

        val encodedData = ComponentDataUtils.encode(
            data
        )

        actionRow {
            selectMenu(PatchFanArtSelectBadgesSelectMenuExecutor, encodedData) {
                this.allowedValues = 0..FanArtTag.values().size

                for (tag in FanArtTag.values()) {
                    option(tag.name, tag.ordinal.toString()) {
                        if (tag in data.tags) {
                            default = true
                        }
                    }
                }
            }
        }

        actionRow {
            interactiveButton(
                ButtonStyle.Success,
                "Atualizar",
                PatchFanArtOnGalleryButtonExecutor,
                ComponentDataUtils.encode(
                    data
                )
            )
        }
    }
}