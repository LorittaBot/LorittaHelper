package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener

class FanArtsOverrideGetExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(FanArtsOverrideGetExecutor::class)

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = "Override: ${ApproveFanArtListener.fanArtOverrideSettings}"
        }
    }
}