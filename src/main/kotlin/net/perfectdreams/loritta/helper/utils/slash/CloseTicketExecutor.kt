package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.value
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData

class CloseTicketExecutor(val helper: LorittaHelperKord) : SlashCommandExecutor() {
    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val channelId = context.channelId
        val channel = helper.channelsCache.getChannel(channelId)
        if (channel.type != ChannelType.PrivateThread) {
            context.sendEphemeralMessage {
                content = "You aren't in a ticket!"
            }
            return
        }

        val parentChannelId = channel.parentId.value ?: return

        val ticketSystemInformation = helper.ticketUtils.systems[parentChannelId]!!
        val i18nContext = ticketSystemInformation.getI18nContext(helper.languageManager)

        context.sendEphemeralMessage {
            content = i18nContext.get(I18nKeysData.Tickets.ClosingYourTicket)
        }

        context.sendMessage {
            content = i18nContext.get(I18nKeysData.Tickets.TicketClosed("<@${context.sender.id.value}>"))
        }

        helper.helperRest.channel.patchThread(
            context.channelId,
            ChannelModifyPatchRequest(
                archived = true.optional()
            ),
            "Archival request via command by ${context.sender.username}#${context.sender.discriminator} (${context.sender.id.value})"
        )
    }
}