package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper

class BroadcastDailyShopWinnersCommand(helper: LorittaHelper) : HelperSlashCommand(helper, this) {
    companion object : SlashCommandDeclaration(
        name = "broadcastdailyshopwinners",
        description = "Envia os vencedores do evento mensal da loja diária"
    )

    override suspend fun executesHelper(context: SlashCommandContext) {
        context.sendMessage {
            content = "Enviando vencedores..."

            flags = MessageFlags(MessageFlag.Ephemeral)
        }

        helper.dailyShopWinners?.broadcastDailyShopWinners()
    }
}