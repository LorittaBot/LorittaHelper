package net.perfectdreams.loritta.helper.utils.checkillegalnitrosell

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.toNaiveBayesClassifier
import net.perfectdreams.loritta.helper.utils.splitWords

/**
 * Checks if a message contains *bad* stuff about trading sonhos to Nitro
 */
class CheckIllegalNitroSell {
    class DiscordMessage(val message: String, val isSpam: Boolean)

    val messages = LorittaHelper::class.java.getResourceAsStream("/selling-sonhos-spam.txt")
            .readAllBytes().toString(Charsets.UTF_8).lines().map {
                DiscordMessage(it, true)
            } + LorittaHelper::class.java.getResourceAsStream("/good-messages.txt")
            .readAllBytes().toString(Charsets.UTF_8).lines().map {
                DiscordMessage(it, false)
            }

    val nbc = messages.toNaiveBayesClassifier(
            featuresSelector = { it.message.splitWords().toSet().also { println(it) } },
            categorySelector = { it.isSpam }
    )

    private val channels = listOf(
            358774895850815488L, 547119872568459284L, 643624690874712094L, 619680554820042752L
        )

    fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot)
            return
        
        if (event.channel.idLong in channels)
            return

        val rawContent = event.message.contentRaw
                .lines()
                .dropWhile { it.startsWith(">") }
                .joinToString(" ")
                .let {
                    MarkdownSanitizer.sanitize(it)
                }

        val input = rawContent.splitWords().toSet()
        val predictedCategory = nbc.predictWithProbability(input)

        if (predictedCategory?.category == true && predictedCategory.probability >= 0.8) {
            val channel = event.jda.getTextChannelById(773912391226359820L)

            channel?.sendMessage(
                    "<:lori_ban_hammer:741058240455901254> **|** ${event.author.asMention} tem ${"%.2f".format(predictedCategory.probability * 100).toDouble()}% de estar querendo vender Nitro por Sonhos na mensagem `${event.message.contentRaw}` <${event.message.jumpUrl}>! Verifique para mim!!"
            )?.queue()
        }
    }
}
