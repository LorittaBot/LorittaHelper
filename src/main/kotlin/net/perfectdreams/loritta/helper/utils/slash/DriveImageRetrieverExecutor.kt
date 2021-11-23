package net.perfectdreams.loritta.helper.utils.slash

import io.ktor.client.request.*
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

            val image = GoogleDriveUtils.retrieveImageFromDrive(url, LorittaHelperKord.http)

            if (image != null) {
                val extension = when (image.mimeType) {
                    "image/png" -> "png"
                    "image/jpeg" -> "jpg"
                    "image/bmp" -> "bmp"
                    else -> "png"
                }

                val downloadedImage = LorittaHelperKord.http.get<ByteArray>(image.url)

                context.sendMessage {
                    content = "(─‿‿─)"
                    addFile("unknown.$extension", downloadedImage.inputStream())
                }
            } else {
                context.sendMessage {
                    content = "Não foi possível baixar a imagem..."
                }
            }
        } else {
            context.sendMessage {
                content = "Link inválido da imagem do Drive"
            }
        }
    }
}