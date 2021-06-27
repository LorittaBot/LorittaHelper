package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclarationBuilder
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.ServerMembersExecutor

object ServerMembersCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "servermembers",
        "Mostra os membros de um servidor"
    ) {
        executor = ServerMembersExecutor
    }
}