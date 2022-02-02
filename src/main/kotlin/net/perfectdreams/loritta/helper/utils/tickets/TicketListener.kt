package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.gateway.Gateway
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.on
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.create.allowedMentions
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.Constants

class TicketListener(private val helper: LorittaHelperKord) {
    fun installAutoReplyToMessagesInTicketListener(gateway: Gateway) = gateway.on<MessageCreate> {
        if (this.message.author.bot.discordBoolean)
            return@on

        // Only in Loritta's support server!
        if (Constants.SUPPORT_SERVER_ID != this.message.guildId.value?.value?.toLong())
            return@on

        val channelId = this.message.channelId
        val channel = helper.channelsCache.getChannel(channelId)
        if (channel.type != ChannelType.PrivateThread)
            return@on

        val parentChannelId = channel.parentId.value ?: return@on

        val systemInfo = TicketUtils.informations[parentChannelId]!!
        if (systemInfo !is TicketUtils.HelpDeskTicketSystemInformation)
            return@on

        val channelResponses = systemInfo.channelResponses
        val i18nContext = systemInfo.getI18nContext(helper.languageManager)

        // We remove any lines starting with > (quote) because this sometimes causes responses to something inside a citation, and that looks kinda bad
        val cleanMessage = this.message.content.lines()
            .dropWhile { it.startsWith(">") }
            .joinToString("\n")

        val responses = channelResponses
            .firstOrNull { it.handleResponse(cleanMessage) }?.getResponse(cleanMessage) ?: return@on

        if (responses.isNotEmpty())
            helper.helperRest.channel.createMessage(
                channelId
            ) {
                // We mention roles in some messages, so we don't want the mention to actually go off!
                allowedMentions {}

                val pleaseCloseTheTicketReply = LorittaReply(
                    i18nContext.get(I18nKeysData.Tickets.AutoResponseSolved),
                    "<:lori_nice:726845783344939028>",
                    mentionUser = false
                )

                content = (responses + pleaseCloseTheTicketReply)
                    .joinToString("\n")
                    { it.build(this@on.message.author) }

                messageReference = this@on.message.id
                failIfNotExists = false

                actionRow {
                    interactiveButton(
                        ButtonStyle.Primary,
                        CloseTicketButtonExecutor,
                        ComponentDataUtils.encode(
                            TicketSystemTypeData(systemInfo.systemType)
                        )
                    ) {
                        label = i18nContext.get(I18nKeysData.Tickets.CloseTicket)

                        emoji = DiscordPartialEmoji(Snowflake(726845783344939028), "lori_nice")
                    }
                }
            }
    }
}