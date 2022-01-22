package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.DriveImageRetrieverExecutor

object DriveImageRetrieverCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "retrievedriveimg",
        "Extrai uma imagem do Google Drive e envia para o Discord"
    ) {
        executor = DriveImageRetrieverExecutor
    }
}