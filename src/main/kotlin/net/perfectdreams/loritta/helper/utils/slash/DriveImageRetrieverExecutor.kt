package net.perfectdreams.loritta.helper.utils.slash

import net.dv8tion.jda.api.utils.FileUpload
import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.HelperExecutor
import net.perfectdreams.loritta.helper.utils.GoogleDriveUtils

class DriveImageRetrieverExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val imageLink = string("drivelink", "Um link de uma imagem no GDrive")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val url = args[options.imageLink]

        if (url.startsWith("https://drive.google.com/file/d/")) {
            context.deferChannelMessage(false)

            val imageData = GoogleDriveUtils.downloadGoogleDriveUrl(url.removeSuffix("/view").substringAfterLast("/"))

            if (imageData != null) {
                context.reply(false) {
                    content = "(─‿‿─)"
                    files += FileUpload.fromData(imageData.inputStream(), "image.png")
                }
            } else {
                context.reply(false) {
                    content = "Link inválido da imagem do Drive"
                }
            }
        } else {
            context.reply(false) {
                content = "Link inválido da imagem do Drive"
            }
        }
    }
}