package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.message.messageCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.MessageCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.AddFanArtToGalleryExecutor

object AddFanArtToGalleryCommand : MessageCommandDeclarationWrapper {
    override fun declaration() = messageCommand("Adicionar Fan Art na Galeria", AddFanArtToGalleryExecutor)
}