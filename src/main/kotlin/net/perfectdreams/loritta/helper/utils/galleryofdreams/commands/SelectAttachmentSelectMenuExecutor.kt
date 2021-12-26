package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.selects.SelectMenuExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.selects.SelectMenuWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class SelectAttachmentSelectMenuExecutor(val m: LorittaHelperKord) : SelectMenuWithDataExecutor {
    companion object : SelectMenuExecutorDeclaration(SelectAttachmentSelectMenuExecutor::class, "select_fa_attach")

    override suspend fun onSelect(user: User, context: ComponentContext, data: String, values: List<String>) {
        context.deferChannelMessageEphemerally()

        val data = ComponentDataUtils.decode<AddFanArtData>(data)

        val message = m.helperRest.channel.getMessage(data.fanArtChannelId, data.fanArtMessageId)

        context.updateMessage {
            apply(
                GalleryOfDreamsUtils.createMessage(
                    data.copy(
                        selectedAttachmentId = Snowflake(values.first().toLong())
                    ),
                    message.attachments
                )
            )
        }
    }
}