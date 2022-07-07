package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.value
import dev.kord.gateway.Gateway
import dev.kord.gateway.GuildMemberRemove
import dev.kord.gateway.on
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import mu.KotlinLogging
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData

class AutoCloseTicketWhenMemberLeavesGuildListener(private val helper: LorittaHelperKord) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun installAutoCloseTicketWhenMemberLeavesGuildListener(gateway: Gateway) = gateway.on<GuildMemberRemove> {
        val user = this.member.user
        val userId = user.id
        val guildId = this.member.guildId
        logger.info { "User $userId left guild $guildId... :(" }

        val ticketThread: DiscordChannel = helper.helperRest.guild.listActiveThreads(guildId)
            .threads
            .firstOrNull {
                val name = it.name.value ?: return@firstOrNull false
                if (!name.contains("(") && !name.contains(")"))
                    return@firstOrNull false

                val onlyTheId = name.substringAfterLast("(").substringBeforeLast(")")
                onlyTheId.toULongOrNull() == userId.value
            } ?: return@on

        val channel = helper.channelsCache.getChannel(ticketThread.id)
        if (channel.type != ChannelType.PrivateThread)
            return@on

        val parentChannelId = channel.parentId.value ?: return@on

        val ticketSystemInformation = helper.ticketUtils.systems[parentChannelId] ?: return@on
        val i18nContext = ticketSystemInformation.getI18nContext(helper.languageManager)

        helper.helperRest.channel.createMessage(
            ticketThread.id
        ) {
            content = i18nContext.get(I18nKeysData.Tickets.TicketAutoClosedUserLeftServer)
        }

        helper.helperRest.channel.patchThread(
            channel.id,
            ChannelModifyPatchRequest(
                archived = true.optional()
            ),
            "Archival request because the creator of the ticket left the server"
        )
    }
}