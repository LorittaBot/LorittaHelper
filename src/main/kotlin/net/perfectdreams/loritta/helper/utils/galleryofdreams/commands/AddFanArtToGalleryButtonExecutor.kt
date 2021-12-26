package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class AddFanArtToGalleryButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(AddFanArtToGalleryButtonExecutor::class, "add_fa_gallery")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        val data = ComponentDataUtils.decode<AddFanArtData>(data)

        context.updateMessage {
            content = "$data"
        }
    }
}