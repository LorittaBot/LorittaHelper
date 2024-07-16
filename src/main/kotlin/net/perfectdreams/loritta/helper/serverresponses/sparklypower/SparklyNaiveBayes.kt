package net.perfectdreams.loritta.helper.serverresponses.sparklypower

import net.perfectdreams.loritta.helper.utils.NaiveBayes
import java.text.Normalizer

class SparklyNaiveBayes {
    val classifier = NaiveBayes<QuestionCategory>()

    fun setup() {
        classifier.train(
            QuestionCategory.BUY_PESADELOS,
            listOf(
                "como ganho pesadelos",
                "como consigo pesadelos",
                "como compra pesadelos"
            )
        )

        classifier.train(
            QuestionCategory.SPARKLY_IP,
            listOf(
                "qual é o IP do SparklyPower"
            )
        )

        classifier.train(
            QuestionCategory.BUY_VIP,
            listOf(
                "como compra VIP no SparklyPower"
            )
        )
    }

    private fun train(category: QuestionCategory, documents: List<String>) = classifier.train(
        category, documents.map { normalizeNaiveBayesInput(it) }
    )

    fun main() {
        val documents = listOf(
            Pair("qual é o IP do SparklyPower", "ip"),
            Pair("manda o IP do SparklyPower", "ip"),
            Pair("como comprar VIP", "vip"),
            Pair("quero comprar VIP", "vip"),
            Pair("como eu protejo um terreno?", "terreno"),
            Pair("como proteger um terreno?", "claim"),
        )

        val classifier = NaiveBayes<String>()
        classifier.train(
            "pesadelos",
            listOf(
                "como ganho pesadelos",
                "como consigo pesadelos",
                "como compra pesadelos",
                "como comprar pesadelos",
                "como posso comprar pesadelos"
            ).map { normalizeNaiveBayesInput(it) }
        )
        classifier.train(documents)
    }

    fun replaceShortenedWordsWithLongWords(source: String) = source
        .replace(Regex("\\bSparkly\\b", RegexOption.IGNORE_CASE), "SparklyPower")
        .replace(Regex("\\bservidor\\b", RegexOption.IGNORE_CASE), "SparklyPower")
        .replace(Regex("\\bserver\\b", RegexOption.IGNORE_CASE), "SparklyPower")
        .replace(Regex("\\bpesa\\b", RegexOption.IGNORE_CASE), "pesadelos")
        .replace(Regex("\\beh\\b", RegexOption.IGNORE_CASE), "é")
        .replace(Regex("\\badissiona\\b", RegexOption.IGNORE_CASE), "adiciona")
        .replace(Regex("\\badissiono\\b", RegexOption.IGNORE_CASE), "adiciono")
        .replace(Regex("\\bcm\\b", RegexOption.IGNORE_CASE), "como")
        .replace(Regex("\\bcnsg\\b", RegexOption.IGNORE_CASE), "consigo")
        .replace(Regex("\\bptg\\b", RegexOption.IGNORE_CASE), "protege")
        .replace(Regex("\\btern\\b", RegexOption.IGNORE_CASE), "terreno")

    fun normalizeNaiveBayesInput(source: String) = source
        .normalize()
        .replace("?", "")
        .replace("!", "")
        .replace(".", "")
        .replace(",", "")
        .trim()

    private fun String.normalize(): String {
        val normalizedString = Normalizer.normalize(this, Normalizer.Form.NFD)
        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        return regex.replace(normalizedString, "")
    }

    enum class QuestionCategory {
        BUY_PESADELOS,
        BUY_VIP,
        SPARKLY_IP
    }
}