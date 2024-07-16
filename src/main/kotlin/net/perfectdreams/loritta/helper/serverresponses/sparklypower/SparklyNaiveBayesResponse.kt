package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import mu.KotlinLogging
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.utils.splitWords

abstract class SparklyNaiveBayesResponse(
    private val category: SparklyNaiveBayes.QuestionCategory,
    private val sparklyNaiveBayes: SparklyNaiveBayes
) : LorittaResponse {
    private val logger = KotlinLogging.logger {}

    override fun handleResponse(message: String): Boolean {
        val normalizedMessage = sparklyNaiveBayes.normalizeNaiveBayesInput(sparklyNaiveBayes.replaceShortenedWordsWithLongWords(message))

        // Message too small, bail out!
        if (2 >= normalizedMessage.splitWords().toList().size)
            return false

        val classifications = sparklyNaiveBayes.classifier.detailedClassification(normalizedMessage)
            .entries
            .sortedBy { it.value }

        logger.info { "Results for $normalizedMessage: $classifications" }

        // Get the best classification that matches our message
        val bestMatch = classifications.last()
        // Not the same category? Bail out!
        if (bestMatch.key != category)
            return false

        val secondBestMatch = classifications[classifications.size - 2]
        val diffBetweenBestMatchAndSecondBestMatch = bestMatch.value - secondBestMatch.value

        // We compare between the second best because if two questions are very similar, then the question is a bit confusing
        return bestMatch.value >= 0.5 && diffBetweenBestMatchAndSecondBestMatch >= 0.2
    }
}