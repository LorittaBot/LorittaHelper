package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.optional.optional
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.context.components.GuildComponentContext
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class CloseTicketButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(CloseTicketButtonExecutor::class, "close_ticket")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        if (context is GuildComponentContext) {
            val ticketLanguageData = ComponentDataUtils.decode<TicketLanguageData>(data)
            val language = ticketLanguageData.language.getI18nContext(m)

            context.sendEphemeralMessage {
                content = language.get(I18nKeysData.Tickets.ClosingYourTicket)
            }

            m.helperRest.channel.patchThread(
                context.channelId,
                ChannelModifyPatchRequest(
                    archived = true.optional()
                )
            )
        }
    }
}