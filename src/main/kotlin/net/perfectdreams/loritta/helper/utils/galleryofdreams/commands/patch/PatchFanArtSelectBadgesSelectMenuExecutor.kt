package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch

import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutorDeclaration
import dev.kord.core.entity.User
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutor
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.FanArtTag
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.GalleryOfDreamsUtils

class PatchFanArtSelectBadgesSelectMenuExecutor(val m: LorittaHelperKord, galleryOfDreamsClient: GalleryOfDreamsClient) : SelectMenuExecutor {
    // "Patch Fan Art"
    companion object : SelectMenuExecutorDeclaration("select_pfa_badges")

    override suspend fun onSelect(user: User, context: ComponentContext, values: List<String>) {
        val data = ComponentDataUtils.decode<PatchFanArtData>(context.data)

        context.updateMessage {
            apply(
                GalleryOfDreamsUtils.createPatchFanArtMessage(
                    data.copy(
                        tags = values.map { FanArtTag.values()[it.toInt()] }
                    ),
                )
            )
        }
    }
}