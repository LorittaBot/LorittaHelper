package net.perfectdreams.loritta.helper.utils.generateserverreport

import dev.kord.common.entity.optional.first
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import dev.kord.core.entity.User
import net.perfectdreams.discordinteraktions.platforms.kord.entities.messages.KordPublicMessage
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.GoogleDriveUtils

class ShowFilesExecutor(val m: LorittaHelperKord) : ButtonExecutor {
    companion object : ButtonExecutorDeclaration(ShowFilesExecutor::class, "show_files")

    override suspend fun onClick(user: User, context: ComponentContext) {
        val m = context.message as KordPublicMessage
        val imagesField = m.data.embeds.first().fields.first { it.name == "Imagens" }

        context.sendMessage {
            content = imagesField.value
                .split("\n")
                .joinToString("\n") { GoogleDriveUtils.getEmbeddableDirectGoogleDriveUrl(it.removeSuffix("/view").substringAfterLast("/")) }
        }
    }
}