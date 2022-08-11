package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch

import net.perfectdreams.discordinteraktions.common.components.ButtonExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import dev.kord.core.entity.User
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.galleryofdreams.common.data.api.PatchFanArtRequest
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class PatchFanArtOnGalleryButtonExecutor(val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : ButtonExecutor {
    companion object : ButtonExecutorDeclaration("add_pfa_gallery")

    override suspend fun onClick(user: User, context: ComponentContext) {
        val patchFanArtData = ComponentDataUtils.decode<PatchFanArtData>(context.data)

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