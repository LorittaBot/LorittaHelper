package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.builder.message.MessageBuilder
import net.perfectdreams.discordinteraktions.common.builder.message.actionRow
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.discordinteraktions.common.components.selectMenu
import net.perfectdreams.galleryofdreams.common.FanArtTag
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

object GalleryOfDreamsUtils {
    val ALLOWED_ROLES = listOf(
        Snowflake(924649809103691786L)
    )

    fun createMessage(
        data: AddFanArtData,
        attachments: List<DiscordAttachment>
    ): MessageBuilder.() -> (Unit) = {
        content = "Configure as informações da Fan Art!"

        val encodedData = ComponentDataUtils.encode(
            data
        )

        actionRow {
            selectMenu(SelectAttachmentSelectMenuExecutor, encodedData) {
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
            selectMenu(SelectBadgesSelectMenuExecutor, encodedData) {
                this.allowedValues = 0..FanArtTag.values().size

                for (tag in FanArtTag.values()) {
                    option(tag.name, tag.ordinal.toString()) {
                        default = true
                    }
                }
            }
        }

        actionRow {
            interactiveButton(
                ButtonStyle.Success,
                "Adicionar",
                AddFanArtToGalleryButtonExecutor,
                ComponentDataUtils.encode(
                    data
                )
            )
        }
    }
}