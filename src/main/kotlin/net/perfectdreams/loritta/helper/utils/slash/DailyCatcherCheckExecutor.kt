package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.HelperExecutor
import kotlin.concurrent.thread

class DailyCatcherCheckExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.ADMIN) {
    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.reply(true) {
            content = "Verificando contas fakes..."
        }

        thread {
            helper.dailyCatcherManager?.doReports()
        }
    }
}