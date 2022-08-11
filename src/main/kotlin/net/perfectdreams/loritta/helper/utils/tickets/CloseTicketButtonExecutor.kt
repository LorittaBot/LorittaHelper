package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.optional.optional
import dev.kord.core.entity.User
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import net.perfectdreams.discordinteraktions.common.components.*
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class CloseTicketButtonExecutor(val m: LorittaHelperKord) : ButtonExecutor {
    companion object : ButtonExecutorDeclaration(CloseTicketButtonExecutor::class, "close_ticket")

    override suspend fun onClick(user: User, context: ComponentContext) {
        if (context is GuildComponentContext) {
            val ticketSystemTypeData = ComponentDataUtils.decode<TicketSystemTypeData>(context.data)
            val systemInfo = m.ticketUtils.getSystemBySystemType(ticketSystemTypeData.systemType)
            val language = systemInfo.getI18nContext(m.languageManager)

            context.sendEphemeralMessage {
                content = language.get(I18nKeysData.Tickets.ClosingYourTicket)
            }

            context.sendMessage {
                content = language.get(I18nKeysData.Tickets.TicketClosed("<@${user.id.value}>"))
            }

            m.helperRest.channel.patchThread(
                context.channelId,
                ChannelModifyPatchRequest(
                    archived = true.optional()
                ),
                "Archival request via button by ${user.username}#${user.discriminator} (${user.id.value})"
            )
        }
    }
}