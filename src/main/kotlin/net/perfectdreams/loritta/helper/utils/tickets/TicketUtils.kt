package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import net.perfectdreams.loritta.helper.serverresponses.EnglishResponses
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.serverresponses.PortugueseResponses
import net.perfectdreams.loritta.helper.utils.LanguageManager

object TicketUtils {
    val PORTUGUESE_HELP_DESK_CHANNEL_ID = Snowflake(891834050073997383L)
    val ENGLISH_HELP_DESK_CHANNEL_ID = Snowflake(891834950159044658L)
    val FIRST_FAN_ART_CHANNEL_ID = Snowflake(938247721775661086L)

    val informations = mapOf(
        // Portuguese Help Desk Channel
        PORTUGUESE_HELP_DESK_CHANNEL_ID to HelpDeskTicketSystemInformation(
            TicketSystemType.HELP_DESK_PORTUGUESE,
            LanguageName.PORTUGUESE,
            PortugueseResponses.responses,
            Snowflake(761337893951635458),
            Snowflake(752294116708319324),
            Snowflake(421325387889377291)
        ),

        // English Help Desk Channel
        ENGLISH_HELP_DESK_CHANNEL_ID to HelpDeskTicketSystemInformation(
            TicketSystemType.HELP_DESK_ENGLISH,
            LanguageName.ENGLISH,
            EnglishResponses.responses,
            Snowflake(761337709720633392),
            Snowflake(761385919479414825),
            Snowflake(761586798971322370)
        ),

        // Portuguese First Fan Art Channel
        FIRST_FAN_ART_CHANNEL_ID to FirstFanArtTicketSystemInformation(
            TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE,
            LanguageName.PORTUGUESE,
            Snowflake(924649809103691786),
            Snowflake(557629480391409666)
        ),
    )

    fun getInformationBySystemType(type: TicketSystemType) = informations.values.first { it.systemType == type }

    sealed class TicketSystemInformation(
        val systemType: TicketSystemType,
        val language: LanguageName,
        val archiveDuration: ArchiveDuration
    ) {
        fun getI18nContext(languageManager: LanguageManager) = when (language) {
            LanguageName.PORTUGUESE -> languageManager.getI18nContextById("pt")
            LanguageName.ENGLISH -> languageManager.getI18nContextById("en")
        }
    }

    class HelpDeskTicketSystemInformation(
        systemType: TicketSystemType,
        language: LanguageName,
        val channelResponses: List<LorittaResponse>,
        val faqChannelId: Snowflake,
        val statusChannelId: Snowflake,
        val supportRoleId: Snowflake
    ) : TicketSystemInformation(systemType, language, ArchiveDuration.Day)

    class FirstFanArtTicketSystemInformation(
        systemType: TicketSystemType,
        language: LanguageName,
        val fanArtsManagerRoleId: Snowflake,
        val fanArtRulesChannelId: Snowflake
    ) : TicketSystemInformation(systemType, language, ArchiveDuration.Week)

    enum class TicketSystemType {
        HELP_DESK_PORTUGUESE,
        HELP_DESK_ENGLISH,
        FIRST_FAN_ARTS_PORTUGUESE
    }

    enum class LanguageName {
        PORTUGUESE,
        ENGLISH
    }
}