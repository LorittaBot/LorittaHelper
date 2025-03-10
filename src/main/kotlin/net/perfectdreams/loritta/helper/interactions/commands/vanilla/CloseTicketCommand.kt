package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.perfectdreams.i18nhelper.core.I18nContext
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

            if (channel !is ThreadChannel) {
                context.reply(true) {
                    content = "Você não está em um ticket!"
                }

                return
            }

            val parentChannel = channel.parentChannel

            val i18nContext = if (helper.ticketUtils.systems[parentChannel.idLong] == null) {
                helper.languageManager.getI18nContextById("pt")
            } else {
                helper.ticketUtils.systems[parentChannel.idLong]!!.getI18nContext(helper.languageManager)
            }

            closeTicket(context, channel, i18nContext)
        }
    }

    private suspend fun closeTicket(context: ApplicationCommandContext, channel: ThreadChannel, i18nContext: I18nContext) {
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