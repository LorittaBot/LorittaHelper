package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.DriveImageRetrieverExecutor

object DriveImageRetrieverCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "retrievedriveimg",
        "Extrai uma imagem do Google Drive e envia para o Discord"
    ) {
        executor = DriveImageRetrieverExecutor
    }
}