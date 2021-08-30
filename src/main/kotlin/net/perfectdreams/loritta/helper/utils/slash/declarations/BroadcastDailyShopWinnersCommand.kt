package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.commands.slash.slashCommand
import net.perfectdreams.discordinteraktions.declarations.commands.wrappers.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.helper.utils.slash.BroadcastDailyShopWinnersExecutor

object BroadcastDailyShopWinnersCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "broadcastdailyshopwinners",
        "Envia os vencedores do evento mensal da loja diária"
    ) {
        executor = BroadcastDailyShopWinnersExecutor
    }
}