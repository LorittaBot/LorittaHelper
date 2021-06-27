package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener

class FanArtsOverrideGetExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(FanArtsOverrideGetExecutor::class)

    override suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments) {
        context.sendMessage {
            content = "Override: ${ApproveFanArtListener.fanArtOverrideSettings}"

            isEphemeral = true
        }
    }
}