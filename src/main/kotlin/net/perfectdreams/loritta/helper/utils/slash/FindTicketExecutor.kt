package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class FindTicketExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    inner class Options : ApplicationCommandOptions() {
        val user = user("user", "O usuário que eu irei encontrar o ticket")

        val type = string("type", "O tipo da mensagem") {
            choice("Suporte (Inglês)", TicketUtils.TicketSystemType.HELP_DESK_ENGLISH.name)
            choice("Suporte (Português)", TicketUtils.TicketSystemType.HELP_DESK_PORTUGUESE.name)
            choice("Primeira Fan Art (Português)", TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE.name)
        }
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val user = args[options.user]
        val ticketSystemType = TicketUtils.TicketSystemType.valueOf(args[options.type])
        val cache = helper.ticketUtils.getSystemBySystemType(ticketSystemType).cache
        val cachedTicketId = cache.tickets[user.id]

        context.sendEphemeralMessage {
            content = if (cachedTicketId != null) {
                "Ticket do usuário em ${ticketSystemType}: <#${cachedTicketId.id.value}> https://discord.com/channels/${cache.guildId}/${cachedTicketId.id.value}/0"
            } else {
                "O usuário não possui um ticket em ${ticketSystemType}!"
            }
        }
    }
}