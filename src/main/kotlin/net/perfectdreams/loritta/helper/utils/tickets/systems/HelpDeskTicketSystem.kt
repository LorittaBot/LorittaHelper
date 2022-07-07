package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.RestClient
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

sealed class HelpDeskTicketSystem(
    rest: RestClient,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Snowflake,
    channelId: Snowflake,
    val channelResponses: List<LorittaResponse>,
    val faqChannelId: Snowflake,
    val statusChannelId: Snowflake,
    val supportRoleId: Snowflake
) : TicketSystem(rest, systemType, language, guildId, channelId, ArchiveDuration.Day)