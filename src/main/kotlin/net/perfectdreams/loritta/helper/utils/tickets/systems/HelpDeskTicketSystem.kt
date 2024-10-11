package net.perfectdreams.loritta.helper.utils.tickets.systems

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
) : TicketSystem(jda, systemType, language, guildId, channelId, ThreadChannel.AutoArchiveDuration.TIME_3_DAYS)