package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener

class FanArtsOverrideResetExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(FanArtsOverrideResetExecutor::class)
    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        ApproveFanArtListener.fanArtOverrideSettings = null

        context.sendEphemeralMessage {
            content = "Override resetado!"
        }
    }
}