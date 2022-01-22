package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.ServerMembersExecutor

object ServerMembersCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "servermembers",
        "Mostra os membros de um servidor"
    ) {
        executor = ServerMembersExecutor
    }
}