package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.RestClient
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

sealed class HelpDeskTicketSystem(
    jda: JDA,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Long,
    channelId: Long,
    val channelResponses: List<LorittaResponse>,
    val faqChannelId: Long,
    val statusChannelId: Long,
    val supportRoleId: Long
) : TicketSystem(jda, systemType, language, guildId, channelId, ThreadChannel.AutoArchiveDuration.TIME_24_HOURS)