package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations

import net.perfectdreams.discordinteraktions.common.commands.MessageCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.messageCommand
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.AddFanArtToGalleryMessageExecutor

object AddFanArtToGalleryMessageCommand : MessageCommandDeclarationWrapper {
    override fun declaration() = messageCommand("Adicionar Fan Art na Galeria", AddFanArtToGalleryMessageExecutor)
}