package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.optional.optional
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import net.perfectdreams.discordinteraktions.common.components.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.GuildComponentContext
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class CloseTicketButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(CloseTicketButtonExecutor::class, "close_ticket")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        if (context is GuildComponentContext) {
            val ticketSystemTypeData = ComponentDataUtils.decode<TicketSystemTypeData>(data)
            val systemInfo = TicketUtils.getInformationBySystemType(ticketSystemTypeData.systemType)
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
                "Archival request via button by ${user.name}#${user.discriminator} (${user.id.value})"
            )
        }
    }
}