package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add

import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutorDeclaration
import dev.kord.core.entity.User
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutor
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.FanArtTag
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.GalleryOfDreamsUtils
import java.util.*

class AddFanArtSelectBadgesSelectMenuExecutor(val m: LorittaHelperKord, galleryOfDreamsClient: GalleryOfDreamsClient) :
    SelectMenuExecutor {
    companion object : SelectMenuExecutorDeclaration(AddFanArtSelectBadgesSelectMenuExecutor::class, "select_fa_badges")

    override suspend fun onSelect(user: User, context: ComponentContext, values: List<String>) {
        val data = GalleryOfDreamsUtils.CACHED_DATA[UUID.fromString(context.data)] ?: error("Unknown Data")

        val message = m.helperRest.channel.getMessage(data.fanArtChannelId, data.fanArtMessageId)

        context.updateMessage {
            apply(
                GalleryOfDreamsUtils.createAddFanArtMessage(
                    when (data) {
                        is AddFanArtToExistingArtistData -> data.copy(
                            tags = values.map { FanArtTag.values()[it.toInt()] }
                        )
                        is AddFanArtToNewArtistData -> data.copy(
                            tags = values.map { FanArtTag.values()[it.toInt()] }
                        )
                    },
                    message.attachments
                )
            )
        }
    }
}