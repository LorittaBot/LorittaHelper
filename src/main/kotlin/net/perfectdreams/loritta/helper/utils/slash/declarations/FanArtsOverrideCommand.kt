package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.FanArtsOverrideGetExecutor
import net.perfectdreams.loritta.helper.utils.slash.FanArtsOverrideResetExecutor
import net.perfectdreams.loritta.helper.utils.slash.FanArtsOverrideSetExecutor

object FanArtsOverrideCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "fanartsoverride",
        "Cria ou reseta data overrides para fan arts a serem adicionadas"
    ) {
        subcommand(
            "set",
            "Cria data overrides para fan arts a serem adicionadas"
        ) {
            executor = FanArtsOverrideSetExecutor
        }
        subcommand(
            "reset",
            "Reseta o data override atual"
        ) {
            executor = FanArtsOverrideResetExecutor
        }
        subcommand(
            "get",
        "Mostra o data override atual") {
            executor = FanArtsOverrideGetExecutor
        }
    }
}