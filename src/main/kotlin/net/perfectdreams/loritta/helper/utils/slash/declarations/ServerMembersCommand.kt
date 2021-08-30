package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.ServerMembersExecutor

object ServerMembersCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "servermembers",
        "Mostra os membros de um servidor"
    ) {
        executor = ServerMembersExecutor
    }
}