package net.perfectdreams.loritta.helper.utils.generateserverreport

import dev.kord.common.entity.optional.first
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.discordinteraktions.platforms.kord.entities.messages.KordPublicMessage
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.GoogleDriveUtils

class ShowFilesExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(ShowFilesExecutor::class, "show_files")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        val m = context.message as KordPublicMessage
        val imagesField = m.handle.embeds.first().fields.first { it.name == "Imagens" }

        context.sendMessage {
            content = imagesField.value
                .split("\n")
                .joinToString("\n") { GoogleDriveUtils.getEmbeddableDirectGoogleDriveUrl(it.substringAfterLast("/")) }
        }
    }
}