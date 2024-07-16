package net.perfectdreams.loritta.helper.serverresponses

import net.perfectdreams.loritta.api.messages.LorittaReply

interface LorittaResponse {
    /**
     * Priority is used to avoid responses overlapping another
     * Example: When a response is more "open" and can match a lot of variations, while another response is more "closed" and is more specific.
     *
     * Responses are sorted by higher priority -> lower priority
     */
    val priority: Int
        get() = 0

    /**
     * Checks if this response matches the [message]
     *
     * @see handleResponse
     * @return if the response matches the question
     */
    fun handleResponse(message: String): Boolean

    /**
     * Gets all the [LorittaReply] messages of this response
     *
     * @return a list (can be empty) of [LorittaReply] of this response
     */
    fun getResponse(message: String): List<LorittaReply> {
        return emptyList()
    }

    /**
     * Gets all the [LorittaReply] messages of this response
     *
     * @return a list (can be empty) of [LorittaReply] of this response
     */
    fun getSupportResponse(message: String): AutomatedSupportResponse {
        return AutomatedSupportResponse(getResponse(message), true)
    }
}