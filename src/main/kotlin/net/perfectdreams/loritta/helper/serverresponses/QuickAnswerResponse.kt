package net.perfectdreams.loritta.helper.serverresponses

/**
 * Represents a Helper's response for the help desk system select menu
 */
abstract class QuickAnswerResponse : LorittaResponse {
    // Always false, we don't want to match any messages because this is only used for the select menu
    override fun handleResponse(message: String) = false
}