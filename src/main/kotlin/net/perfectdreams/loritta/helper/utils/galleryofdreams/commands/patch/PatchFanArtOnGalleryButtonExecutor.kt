package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch

import net.perfectdreams.discordinteraktions.common.components.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.data.api.PatchFanArtRequest
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class PatchFanArtOnGalleryButtonExecutor(val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration("add_pfa_gallery")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        val patchFanArtData = ComponentDataUtils.decode<PatchFanArtData>(data)

        context.deferUpdateMessage()

        context.updateMessage {
            content = "Atualizando Fan Art... <a:SCLOADING:715824432450633749>"
        }

        val checkResult = galleryOfDreamsClient.patchFanArt(
            patchFanArtData.fanArtSlug,
            PatchFanArtRequest(
                patchFanArtData.tags
            )
        )

        context.updateMessage {
            content = "Fan Art atualizada! <:gabriela_brush:727259143903248486>"

            // Remove action rows
            components = mutableListOf()
        }
    }
}