package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper
import kotlin.concurrent.thread

class DailyCatcherCheckCommand(helper: LorittaHelper) : HelperSlashCommand(helper, this) {
    companion object : SlashCommandDeclaration(
        name = "dailycatchercheck",
        description = "Contas Fakes, temos que pegar!"
    )

    override suspend fun executesHelper(context: SlashCommandContext) {
        context.sendMessage {
            content = "Verificando contas fakes..."

            flags = MessageFlags(MessageFlag.Ephemeral)
        }

        thread {
            helper.dailyCatcherManager?.doReports()
        }
    }
}