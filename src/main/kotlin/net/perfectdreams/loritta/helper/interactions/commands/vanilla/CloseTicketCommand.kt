package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.morenitta.interactions.commands.*

class CloseTicketCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand("closeticket", "Closes a ticket") {
        executor = CloseTicketExecutor()
    }

    inner class CloseTicketExecutor : LorittaSlashCommandExecutor() {
        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            val channel = context.event.guildChannel
            if (channel.type != ChannelType.GUILD_PRIVATE_THREAD) {
                context.reply(true) {
                    content = "You aren't in a ticket!"
                }
                return
            }
            channel as ThreadChannel

            val parentChannel = channel.parentChannel

            val ticketSystemInformation = helper.ticketUtils.systems[parentChannel.idLong]!!
            val i18nContext = ticketSystemInformation.getI18nContext(helper.languageManager)

            context.reply(true) {
                content = i18nContext.get(I18nKeysData.Tickets.ClosingYourTicket)
            }

            context.reply(false) {
                content = i18nContext.get(I18nKeysData.Tickets.TicketClosed(context.user.asMention))
            }

            channel.manager.setArchived(true)
                .reason("Archival request via command by ${context.user.name} (${context.user.idLong})")
                .await()
        }
    }
}