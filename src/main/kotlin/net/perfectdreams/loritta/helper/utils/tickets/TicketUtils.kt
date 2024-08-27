package net.perfectdreams.loritta.helper.utils.tickets

import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.loritta.EnglishResponses
import net.perfectdreams.loritta.helper.serverresponses.loritta.PortugueseResponses
import net.perfectdreams.loritta.helper.serverresponses.sparklypower.SparklyPowerResponses
import net.perfectdreams.loritta.helper.utils.tickets.systems.FirstFanArtTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.LorittaHelpDeskTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.SparklyPowerHelpDeskTicketSystem

class TicketUtils(val m: LorittaHelper) {
    private val community = m.config.guilds.community
    private val english = m.config.guilds.english
    private val sparklyPower = m.config.guilds.sparklyPower

    private val portugueseResponses = PortugueseResponses(m.config)
    private val englishResponses = EnglishResponses(m.config)

    val systems = mapOf(
        // Portuguese Help Desk Channel
        community.channels.support to LorittaHelpDeskTicketSystem(
            m.jda,
            TicketSystemType.HELP_DESK_PORTUGUESE,
            LanguageName.PORTUGUESE,
            community.id,
            community.channels.support,
            portugueseResponses.responses,
            community.channels.faq,
            community.channels.status,
            community.roles.support
        ),

        // English Help Desk Channel
        english.channels.support to LorittaHelpDeskTicketSystem(
            m.jda,
            TicketSystemType.HELP_DESK_ENGLISH,
            LanguageName.ENGLISH,
            english.id,
            english.channels.support,
            englishResponses.responses,
            english.channels.faq,
            english.channels.status,
            english.roles.englishSupport
        ),

        // Portuguese First Fan Art Channel
        community.channels.firstFanArt to FirstFanArtTicketSystem(
            m.jda,
            TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE,
            LanguageName.PORTUGUESE,
            community.id,
            community.channels.firstFanArt,
            community.roles.firstFanArtManager,
            community.channels.firstFanArtRules
        ),

        // SparklyPower Help Desk Channel
        sparklyPower.channels.support to SparklyPowerHelpDeskTicketSystem(
            m.jda,
            TicketSystemType.SPARKLYPOWER_HELP_DESK_PORTUGUESE,
            LanguageName.PORTUGUESE,
            sparklyPower.id,
            sparklyPower.channels.support,
            SparklyPowerResponses.responses,
            sparklyPower.channels.faq,
            sparklyPower.channels.status,
            sparklyPower.roles.staff
        ),
    )

    fun getSystemBySystemType(type: TicketSystemType) = systems.values.first { it.systemType == type }

    enum class TicketSystemType {
        HELP_DESK_PORTUGUESE,
        HELP_DESK_ENGLISH,
        FIRST_FAN_ARTS_PORTUGUESE,
        SPARKLYPOWER_HELP_DESK_PORTUGUESE
    }

    enum class LanguageName {
        PORTUGUESE,
        ENGLISH
    }
}
