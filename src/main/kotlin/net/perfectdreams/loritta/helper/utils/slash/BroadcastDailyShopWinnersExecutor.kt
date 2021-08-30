package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper

class BroadcastDailyShopWinnersExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(BroadcastDailyShopWinnersExecutor::class)

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = "Enviando vencedores..."
        }

        helper.dailyShopWinners?.broadcastDailyShopWinners()
    }
}