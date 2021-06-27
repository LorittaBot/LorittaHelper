package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.BroadcastDailyShopWinnersExecutor

object BroadcastDailyShopWinnersCommand: SlashCommandDeclaration {
    override fun declaration() = slashCommand(
        "broadcastdailyshopwinners",
        "Envia os vencedores do evento mensal da loja diária"
    ) {
        executor = BroadcastDailyShopWinnersExecutor
    }
}