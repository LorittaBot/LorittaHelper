package net.perfectdreams.loritta.helper.utils.slash.declarations

import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.ServerMembersExecutor

class ServerMembersCommand(val helper: LorittaHelperKord, val rest: RestClient) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "servermembers",
        "Mostra os membros de um servidor"
    ) {
        executor = ServerMembersExecutor(helper, rest)
    }
}