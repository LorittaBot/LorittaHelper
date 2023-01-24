package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.GoogleDriveUtils

class DriveImageRetrieverExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val imageLink = string("drivelink", "Um link de uma imagem no GDrive")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val url = args[options.imageLink]

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