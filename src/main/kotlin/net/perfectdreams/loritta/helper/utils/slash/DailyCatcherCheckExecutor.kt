package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper
import kotlin.concurrent.thread

class DailyCatcherCheckExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(DailyCatcherCheckExecutor::class)

    override suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments) {
        context.sendMessage {
            content = "Verificando contas fakes..."

            isEphemeral = true
        }

        thread {
            helper.dailyCatcherManager?.doReports()
        }
    }
}