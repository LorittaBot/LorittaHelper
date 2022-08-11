package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGallerySlashExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtSlashExecutor

class GalleryOfDreamsSlashCommand(val helper: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand("galleryofdreams", "Comandos relacionados a Galeria dos Sonhos") {
        subcommand("add", "Adiciona uma Fan Art na Galeria dos Sonhos pelo link dela") {
            executor = AddFanArtToGallerySlashExecutor(helper, galleryOfDreamsClient)
        }

        subcommand("patch", "Altera as informações de uma Fan Art na Galeria dos Sonhos") {
            executor = PatchFanArtSlashExecutor(helper, galleryOfDreamsClient)
        }
    }
}