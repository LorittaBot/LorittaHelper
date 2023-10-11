package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.Snowflake
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.serverresponses.loritta.EnglishResponses
import net.perfectdreams.loritta.helper.serverresponses.loritta.PortugueseResponses
import net.perfectdreams.loritta.helper.serverresponses.sparklypower.SparklyPowerResponses
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.tickets.systems.FirstFanArtTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.LorittaHelpDeskTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.SparklyPowerHelpDeskTicketSystem

class TicketUtils(val m: LorittaHelperKord) {
    private val PORTUGUESE_HELP_DESK_CHANNEL_ID = Snowflake(891834050073997383L)
    private val ENGLISH_HELP_DESK_CHANNEL_ID = Snowflake(891834950159044658L)
    private val FIRST_FAN_ART_CHANNEL_ID = Snowflake(938247721775661086L)
    private val SPARKLYPOWER_HELP_DESK_CHANNEL_ID = Snowflake(994664055933517925L)

    val systems = mapOf(
        // Portuguese Help Desk Channel
        PORTUGUESE_HELP_DESK_CHANNEL_ID to LorittaHelpDeskTicketSystem(
            m.helperRest,
            TicketSystemType.HELP_DESK_PORTUGUESE,
            LanguageName.PORTUGUESE,
            Snowflake(Constants.COMMUNITY_SERVER_ID),
            Snowflake(1077726822160142386L ),
            PortugueseResponses.responses,
            Snowflake(574308431029207060L),
            Snowflake(610094449737072660L),
            Snowflake(399301696892829706L)
        ),

        // English Help Desk Channel
        ENGLISH_HELP_DESK_CHANNEL_ID to LorittaHelpDeskTicketSystem(
            m.helperRest,
            TicketSystemType.HELP_DESK_ENGLISH,
            LanguageName.ENGLISH,
            Snowflake(Constants.SUPPORT_SERVER_ID),
            Snowflake(891834950159044658),
            EnglishResponses.responses,
            Snowflake(761337709720633392),
            Snowflake(761385919479414825),
            Snowflake(761586798971322370)
        ),

        // Portuguese First Fan Art Channel
        FIRST_FAN_ART_CHANNEL_ID to FirstFanArtTicketSystem(
            m.helperRest,
            TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE,
            LanguageName.PORTUGUESE,
            Snowflake(Constants.COMMUNITY_SERVER_ID),
            Snowflake(938247721775661086),
            Snowflake(924649809103691786),
            Snowflake(557629480391409666)
        ),

        // SparklyPower Help Desk Channel
        SPARKLYPOWER_HELP_DESK_CHANNEL_ID to SparklyPowerHelpDeskTicketSystem(
            m.helperRest,
            TicketSystemType.SPARKLYPOWER_HELP_DESK_PORTUGUESE,
            LanguageName.PORTUGUESE,
            Snowflake(320248230917046282),
            Snowflake(994664055933517925),
            SparklyPowerResponses.responses,
            Snowflake(760262410098442270),
            Snowflake(332866197701918731),
            Snowflake(332650495522897920) // Staff Role
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