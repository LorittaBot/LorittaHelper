package net.perfectdreams.loritta.helper.utils.slash.declarations

import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.RetrieveMessageExecutor

class RetrieveMessageCommand(val helper: LorittaHelperKord, val rest: RestClient) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "retrievemessage",
    "Pega o conteúdo de uma mensagem a partir de um link") {
        executor = RetrieveMessageExecutor(helper, rest)
    }
}