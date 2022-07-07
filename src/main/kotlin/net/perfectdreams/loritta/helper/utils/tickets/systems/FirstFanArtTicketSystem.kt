package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.RestClient
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class FirstFanArtTicketSystem(
    rest: RestClient,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Snowflake,
    channelId: Snowflake,
    val fanArtsManagerRoleId: Snowflake,
    val fanArtRulesChannelId: Snowflake
) : TicketSystem(rest, systemType, language, guildId, channelId, ArchiveDuration.Week)