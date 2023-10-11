package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.utils.extensions.await

class AutoCloseTicketWhenMemberLeavesListener(val m: LorittaHelper) : ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        m.launch {
            val user = event.user
            val userId = user.idLong
            val guildId = event.guild.idLong
            logger.info { "User $userId left guild $guildId... :(" }

            val ticketThread = event.guild.retrieveActiveThreads().await()
                .firstOrNull {
                    val name = it.name
                    if (!name.contains("(") && !name.contains(")"))
                        return@firstOrNull false

                    val onlyTheId = name.substringAfterLast("(").substringBeforeLast(")")
                    onlyTheId.toLong() == userId
                } ?: return@launch

            val parentChannelId = ticketThread.parentChannel.idLong

            val ticketSystemInformation = m.ticketUtils.systems[parentChannelId] ?: return@launch
            val i18nContext = ticketSystemInformation.getI18nContext(m.languageManager)

            ticketThread.sendMessage(i18nContext.get(I18nKeysData.Tickets.TicketAutoClosedUserLeftServer)).await()

            ticketThread.manager.setArchived(true).reason("Archival request because the creator of the ticket left the server").await()
        }
    }
}