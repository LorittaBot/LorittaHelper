package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.utils.slash.BroadcastDailyShopWinnersExecutor

object BroadcastDailyShopWinnersCommand: SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "broadcastdailyshopwinners",
        "Envia os vencedores do evento mensal da loja diária"
    ) {
        executor = BroadcastDailyShopWinnersExecutor
    }
}