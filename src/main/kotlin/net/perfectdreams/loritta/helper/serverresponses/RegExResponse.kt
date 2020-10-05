package net.perfectdreams.loritta.helper.serverresponses

import java.util.regex.Pattern

/**
 * Represents a Helper's response that uses regex for indetification
 * If the regex matches with the sent message, the response from [getResponse] will be sent
 */
abstract class RegExResponse : LorittaResponse {
    companion object {
        const val WHERE_IT_IS = "como|onde|qual|existe|tem( )?jeito|ajuda|quero|queria"
        const val ACTIVATE_OR_CHANGE = "pega|pego|coloc|clc|faço|faco|fasso|alter|boto|bota|ativ"
        const val LORI_NAME = "lori|lorri|297153970613387264"
    }

    /**
     * Patterns used in the [handleResponse] check
     */
    val patterns = mutableListOf<Pattern>()

    /**
     * Handles the RegEx response
     */
    override fun handleResponse(message: String): Boolean = patterns.all {
        it.matcher(message).find()
    }
}