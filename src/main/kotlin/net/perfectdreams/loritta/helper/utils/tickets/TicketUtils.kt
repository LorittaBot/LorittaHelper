package net.perfectdreams.loritta.helper.utils.tickets

import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.loritta.EnglishResponses
import net.perfectdreams.loritta.helper.serverresponses.loritta.PortugueseResponses
import net.perfectdreams.loritta.helper.serverresponses.sparklypower.SparklyPowerResponses
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.tickets.systems.FirstFanArtTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.LorittaHelpDeskTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.SparklyPowerHelpDeskTicketSystem

class TicketUtils(val m: LorittaHelper) {
    private val PORTUGUESE_HELP_DESK_CHANNEL_ID = 1077726822160142386L
    private val ENGLISH_HELP_DESK_CHANNEL_ID = 891834950159044658L
    private val FIRST_FAN_ART_CHANNEL_ID = 938247721775661086L
    private val SPARKLYPOWER_HELP_DESK_CHANNEL_ID = 994664055933517925L

    val systems = mapOf(
        // Portuguese Help Desk Channel
        PORTUGUESE_HELP_DESK_CHANNEL_ID to LorittaHelpDeskTicketSystem(
            m.jda,
            TicketSystemType.HELP_DESK_PORTUGUESE,
            LanguageName.PORTUGUESE,
            Constants.COMMUNITY_SERVER_ID,
            1077726822160142386L,
            PortugueseResponses.responses,
            1191767752894062632L,
            610094449737072660L,
            399301696892829706L
        ),

        // English Help Desk Channel
        ENGLISH_HELP_DESK_CHANNEL_ID to LorittaHelpDeskTicketSystem(
            m.jda,
            TicketSystemType.HELP_DESK_ENGLISH,
            LanguageName.ENGLISH,
            Constants.SUPPORT_SERVER_ID,
            891834950159044658,
            EnglishResponses.responses,
            761337709720633392,
            761385919479414825,
            761586798971322370
        ),

        // Portuguese First Fan Art Channel
        FIRST_FAN_ART_CHANNEL_ID to FirstFanArtTicketSystem(
            m.jda,
            TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE,
            LanguageName.PORTUGUESE,
            Constants.COMMUNITY_SERVER_ID,
            938247721775661086,
            924649809103691786,
            557629480391409666
        ),

        // SparklyPower Help Desk Channel
        SPARKLYPOWER_HELP_DESK_CHANNEL_ID to SparklyPowerHelpDeskTicketSystem(
            m.jda,
            TicketSystemType.SPARKLYPOWER_HELP_DESK_PORTUGUESE,
            LanguageName.PORTUGUESE,
            320248230917046282,
            994664055933517925,
            SparklyPowerResponses.responses,
            760262410098442270,
            332866197701918731,
            332650495522897920 // Staff Role
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
