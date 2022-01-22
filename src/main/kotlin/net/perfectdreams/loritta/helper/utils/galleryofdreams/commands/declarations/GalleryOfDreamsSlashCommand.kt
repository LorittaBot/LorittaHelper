package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.AddFanArtToGallerySlashExecutor

object GalleryOfDreamsSlashCommand : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand("galleryofdreams", "Comandos relacionados a Galeria dos Sonhos") {
        subcommand("add", "Adiciona uma Fan Art na Galeria dos Sonhos pelo link dela") {
            executor = AddFanArtToGallerySlashExecutor
        }
    }
}