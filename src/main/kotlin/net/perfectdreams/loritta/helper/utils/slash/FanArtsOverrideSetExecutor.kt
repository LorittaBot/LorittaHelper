package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener

class FanArtsOverrideSetExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(FanArtsOverrideSetExecutor::class) {
        override val options = Options

        object Options: CommandOptions() {
            val imageFileName = string("image_file_name", "Nome da Fan Art")
                .register()

            val tags = string("tags", "Tags da Fan Art")
                .register()

            val artistFileName = string("artist_file_name", "Nome do Desenhista (Arquivo)")
                .register()

            val artistImageFileName = string("artist_image_file_name", "Nome do Desenhista (Imagem)")
                .register()
        }
    }
    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        with(options) {
            val fileName = args[imageFileName]
            val tags = args[tags].split(",").map { it.trim() }
            val artistFileName = args[artistFileName]
            val artistFileNameOnImage = args[artistImageFileName]

            ApproveFanArtListener.fanArtOverrideSettings = ApproveFanArtListener.FanArtOverrideSettings(
                fileName,
                tags,
                artistFileName,
                artistFileNameOnImage
            )

            context.sendEphemeralMessage {
                content = "Override criado! ${ApproveFanArtListener.fanArtOverrideSettings}"
            }
        }
    }
}