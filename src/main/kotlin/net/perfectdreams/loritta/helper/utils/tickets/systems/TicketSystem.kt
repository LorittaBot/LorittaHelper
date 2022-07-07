package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.RestClient
import net.perfectdreams.loritta.helper.utils.LanguageManager
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.helper.utils.tickets.TicketsCache

sealed class TicketSystem(
    val rest: RestClient,
    val systemType: TicketUtils.TicketSystemType,
    val language: TicketUtils.LanguageName,
    val guildId: Snowflake,
    val channelId: Snowflake,
    val archiveDuration: ArchiveDuration
) {
    val cache = TicketsCache(
        guildId,
        channelId,
        rest
    )

    fun getI18nContext(languageManager: LanguageManager) = when (language) {
        TicketUtils.LanguageName.PORTUGUESE -> languageManager.getI18nContextById("pt")
        TicketUtils.LanguageName.ENGLISH -> languageManager.getI18nContextById("en")
    }
}