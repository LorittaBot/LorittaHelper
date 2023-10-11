package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.minn.jda.ktx.messages.InlineMessage
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel.AutoArchiveDuration
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.helper.utils.LanguageManager
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.helper.utils.tickets.TicketsCache

sealed class TicketSystem(
    val jda: JDA,
    val systemType: TicketUtils.TicketSystemType,
    val language: TicketUtils.LanguageName,
    val guildId: Long,
    val channelId: Long,
    val archiveDuration: AutoArchiveDuration
) {
    abstract val ticketCreatedMessage: InlineMessage<*>.(User, I18nContext) -> Unit

    val cache = TicketsCache(
        jda,
        guildId,
        channelId
    )

    fun getI18nContext(languageManager: LanguageManager) = when (language) {
        TicketUtils.LanguageName.PORTUGUESE -> languageManager.getI18nContextById("pt")
        TicketUtils.LanguageName.ENGLISH -> languageManager.getI18nContextById("en")
    }
}