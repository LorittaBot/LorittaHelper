package net.perfectdreams.loritta.helper.utils.checkillegalnitrosell

import net.dv8tion.jda.api.utils.MarkdownSanitizer
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.toNaiveBayesClassifier

fun main() {
    val messages = LorittaHelper::class.java.getResourceAsStream("/selling-sonhos-spam.txt")
            .readAllBytes().toString(Charsets.UTF_8).lines().map {
                CheckIllegalNitroSell.DiscordMessage(it, true)
            } + LorittaHelper::class.java.getResourceAsStream("/good-messages.txt")
            .readAllBytes().toString(Charsets.UTF_8).lines().map {
                CheckIllegalNitroSell.DiscordMessage(it, false)
            }

    fun String.splitWords() =  split(Regex("\\s")).asSequence()
            .map { it.replace(Regex("[^A-Za-z]"),"").toLowerCase() }
            .filter { it.isNotEmpty() }

    val nbc = messages.toNaiveBayesClassifier(
            featuresSelector = { it.message.splitWords().toSet().also { println(it) } },
            categorySelector = { it.isSpam }
    )

    val rawContent = "eu troco sonhos por nitro interessados dm"
            .lines()
            .dropWhile { it.startsWith(">") }
            .joinToString(" ")
            .let {
                MarkdownSanitizer.sanitize(it)
            }

    val input = rawContent.splitWords().toSet()
    val predictedCategory = nbc.predictWithProbability(input)

    println("${predictedCategory?.category} ${predictedCategory?.probability}")
}