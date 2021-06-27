package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper

class BroadcastDailyShopWinnersExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(BroadcastDailyShopWinnersExecutor::class)

    override suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments) {
        context.sendMessage {
            content = "Enviando vencedores..."

            isEphemeral = true
        }

        helper.dailyShopWinners?.broadcastDailyShopWinners()
    }
}