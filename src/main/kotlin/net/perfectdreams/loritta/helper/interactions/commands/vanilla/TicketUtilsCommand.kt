package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions

class TicketUtilsCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "ticketutils",
        "Ferramentas de administração relacionadas ao sistema de tickets"
    ) {
        subcommand("find", "Encontra o ticket de um usuário") {
            executor = FindTicketExecutor()
        }

        subcommand("info", "Informações sobre o cache de tickets") {
            executor = TicketInfoExecutor()
        }
    }

    inner class FindTicketExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            val user = user("user", "O usuário que eu irei encontrar o ticket")

            val type = string("type", "O tipo da mensagem") {
                choice("Suporte (Inglês)", TicketUtils.TicketSystemType.HELP_DESK_ENGLISH.name)
                choice("Suporte (Português)", TicketUtils.TicketSystemType.HELP_DESK_PORTUGUESE.name)
                choice("Primeira Fan Art (Português)", TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE.name)
            }
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            val user = args[options.user]
            val ticketSystemType = TicketUtils.TicketSystemType.valueOf(args[options.type])
            val cache = helper.ticketUtils.getSystemBySystemType(ticketSystemType).cache
            val cachedTicketId = cache.tickets[user.user.idLong]

            context.reply(true) {
                content = if (cachedTicketId != null) {
                    "Ticket do usuário em ${ticketSystemType}: <#${cachedTicketId.id}> https://discord.com/channels/${cache.guildId}/${cachedTicketId.id}/0"
                } else {
                    "O usuário não possui um ticket em ${ticketSystemType}!"
                }
            }
        }
    }

    inner class TicketInfoExecutor : LorittaSlashCommandExecutor() {
        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.reply(true) {
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
}