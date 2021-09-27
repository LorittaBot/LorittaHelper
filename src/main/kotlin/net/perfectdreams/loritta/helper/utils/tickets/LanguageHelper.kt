package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.Snowflake
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.helper.LorittaHelperKord

fun TicketLanguageData.LanguageName.getI18nContext(
    helper: LorittaHelperKord
): I18nContext {
    return when (this) {
        TicketLanguageData.LanguageName.PORTUGUESE -> helper.languageManager.getI18nContextById("pt")
        TicketLanguageData.LanguageName.ENGLISH -> helper.languageManager.getI18nContextById("en")
    }
}

val TicketLanguageData.LanguageName.supportRoleId: Snowflake
    get() {
        return when (this) {
            TicketLanguageData.LanguageName.PORTUGUESE -> Snowflake(421325387889377291)
            TicketLanguageData.LanguageName.ENGLISH -> Snowflake(761586798971322370)
            else -> TODO("Add support role ID for $this!")
        }
    }

val TicketLanguageData.LanguageName.faqChannelId: Snowflake
    get() {
        return when (this) {
            TicketLanguageData.LanguageName.PORTUGUESE -> Snowflake(761337893951635458)
            TicketLanguageData.LanguageName.ENGLISH -> Snowflake(761337709720633392)
            else -> TODO("Add support FAQ Channel ID for $this!")
        }
    }

val TicketLanguageData.LanguageName.lorittaStatusChannelId: Snowflake
    get() {
        return when (this) {
            TicketLanguageData.LanguageName.PORTUGUESE -> Snowflake(752294116708319324)
            TicketLanguageData.LanguageName.ENGLISH -> Snowflake(761385919479414825)
            else -> TODO("Add Loritta Status Channel ID for $this!")
        }
    }

fun isPortugueseHelpDeskChannel(id: Snowflake) =
    id == Snowflake(891834050073997383) /* Portuguese Help Desk Channel */ || id == Snowflake(891682539968794624) /* Test Ticket Channel */

fun isEnglishHelpDeskChannel(id: Snowflake) =
    id == Snowflake(891834950159044658) /* English Help Desk Channel */