package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener

class FanArtsOverrideSetExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
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
    override suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments) {
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

            context.sendMessage {
                content = "Override criado! ${ApproveFanArtListener.fanArtOverrideSettings}"

                isEphemeral = true
            }
        }
    }
}