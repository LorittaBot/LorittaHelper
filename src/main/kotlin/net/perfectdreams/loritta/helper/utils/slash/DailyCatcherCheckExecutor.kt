package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import kotlin.concurrent.thread

class DailyCatcherCheckExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(DailyCatcherCheckExecutor::class)

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = "Verificando contas fakes..."
        }

        thread {
            helper.dailyCatcherManager?.doReports()
        }
    }
}