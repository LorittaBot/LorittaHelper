package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.SelectMenuWithDataExecutor
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.FanArtTag
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class SelectBadgesSelectMenuExecutor(val m: LorittaHelperKord, galleryOfDreamsClient: GalleryOfDreamsClient) : SelectMenuWithDataExecutor {
    companion object : SelectMenuExecutorDeclaration(SelectBadgesSelectMenuExecutor::class, "select_fa_badges")

    override suspend fun onSelect(user: User, context: ComponentContext, data: String, values: List<String>) {
        val data = ComponentDataUtils.decode<AddFanArtData>(data)

        val message = m.helperRest.channel.getMessage(data.fanArtChannelId, data.fanArtMessageId)

        context.updateMessage {
            apply(
                GalleryOfDreamsUtils.createMessage(
                    data.copy(
                        tags = values.map { FanArtTag.values()[it.toInt()] }
                    ),
                    message.attachments
                )
            )
        }
    }
}