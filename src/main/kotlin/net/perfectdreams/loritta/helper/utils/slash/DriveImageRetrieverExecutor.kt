package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.GoogleDriveUtils
import net.perfectdreams.loritta.helper.utils.slash.DriveImageRetrieverExecutor.Companion.Options.imageLink

class DriveImageRetrieverExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(DriveImageRetrieverExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            val imageLink = string("drivelink", "Um link de uma imagem no GDrive")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val url = args[imageLink]

        if (url.startsWith("https://drive.google.com/file/d/")) {
            context.deferChannelMessage()

            val imageUrl = GoogleDriveUtils.getEmbeddableDirectGoogleDriveUrl(url.removeSuffix("/view").substringAfterLast("/"))

            context.sendMessage {
                content = "(─‿‿─) $imageUrl"
            }
        } else {
            context.sendMessage {
                content = "Link inválido da imagem do Drive"
            }
        }
    }
}