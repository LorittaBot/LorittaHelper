package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord

class BroadcastDailyShopWinnersExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = "Enviando vencedores..."
        }

        helper.dailyShopWinners?.broadcastDailyShopWinners()
    }
}