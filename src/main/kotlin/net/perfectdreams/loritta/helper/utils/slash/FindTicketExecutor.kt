package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class FindTicketExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    companion object : SlashCommandExecutorDeclaration(FindTicketExecutor::class) {
        object Options : ApplicationCommandOptions() {
            val user = user("user", "O usuário que eu irei encontrar o ticket")
                .register()

            val type = string("type", "O tipo da mensagem")
                .choice(TicketUtils.TicketSystemType.HELP_DESK_ENGLISH.name, "Suporte (Inglês)")
                .choice(TicketUtils.TicketSystemType.HELP_DESK_PORTUGUESE.name, "Suporte (Português)")
                .choice(TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE.name, "Primeira Fan Art (Português)")
                .register()
        }

        override val options = Options
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val user = args[options.user]
        val ticketSystemType = TicketUtils.TicketSystemType.valueOf(args[options.type])
        val cache = helper.getTicketsCacheBySystemType(ticketSystemType)
        val cachedTicketId = cache.tickets[user.id]

        context.sendEphemeralMessage {
            content = if (cachedTicketId != null) {
                "Ticket do usuário em ${ticketSystemType}: <#${cachedTicketId.id.value}>"
            } else {
                "O usuário não possui um ticket em ${ticketSystemType}!"
            }
        }
    }
}