package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.message.messageCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.MessageCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.AddFanArtToGalleryExecutor

object AddFanArtToGalleryCommand : MessageCommandDeclarationWrapper {
    override fun declaration() = messageCommand("Adicionar Fan Art na Gallery", AddFanArtToGalleryExecutor)
}