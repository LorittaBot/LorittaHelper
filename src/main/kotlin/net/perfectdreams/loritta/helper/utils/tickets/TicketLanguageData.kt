package net.perfectdreams.loritta.helper.utils.tickets

import kotlinx.serialization.Serializable

@Serializable
data class TicketLanguageData(
    val language: LanguageName
) {
    enum class LanguageName {
        PORTUGUESE,
        ENGLISH
    }
}