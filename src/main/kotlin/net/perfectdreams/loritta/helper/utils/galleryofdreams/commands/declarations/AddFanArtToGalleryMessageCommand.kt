package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations

import net.perfectdreams.discordinteraktions.common.commands.MessageCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.messageCommand
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGalleryMessageExecutor

class AddFanArtToGalleryMessageCommand(val helper: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : MessageCommandDeclarationWrapper {
    override fun declaration() = messageCommand("Adicionar Fan Art na Galeria", AddFanArtToGalleryMessageExecutor(helper, galleryOfDreamsClient))
}