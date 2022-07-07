package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord

class TicketInfoExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    companion object : SlashCommandExecutorDeclaration(TicketInfoExecutor::class)

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = buildString {
                helper.ticketUtils.systems.values.forEach {
                    val type = it.systemType
                    val cache = it.cache

                    append("**${type}:** ${cache.tickets.size} tickets\n")
                }
            }
        }
    }
}