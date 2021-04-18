package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration

object FanArtsOverrideDeclaration {
    object Root : SlashCommandDeclaration(
        name = "fanartsoverride",
        description = "Cria ou reseta data overrides para fan arts a serem adicionadas"
    ) {
        override val options = Options

        object Options : SlashCommandDeclaration.Options() {
            val set = subcommand(Set)
                .register()
            val reset = subcommand(Reset)
                .register()
            val get = subcommand(Get)
                .register()
        }
    }

    object Set : SlashCommandDeclaration(
        name = "set",
        description = "Cria data overrides para fan arts a serem adicionadas"
    ) {
        override val options = Options

        object Options : SlashCommandDeclaration.Options() {
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

    object Reset : SlashCommandDeclaration(
        name = "reset",
        description = "Reseta o data override atual"
    )

    object Get : SlashCommandDeclaration(
        name = "get",
        description = "Mostra o data override atual"
    )
}