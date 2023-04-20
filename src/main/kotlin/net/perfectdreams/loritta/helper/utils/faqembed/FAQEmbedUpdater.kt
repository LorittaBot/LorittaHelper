package net.perfectdreams.loritta.helper.utils.faqembed

import kotlinx.coroutines.delay
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.extensions.retrieveAllMessages
import java.awt.Color

/**
 * Used to automatically update the Frequently Asked Questions embed
 */
abstract class FAQEmbedUpdater(val m: LorittaHelper, val jda: JDA) {
    companion object {
        // Matches "Emoji **| OwO whats this**
        // Group 1 = Emoji
        // Group 2 == OwO whats this
        // Also matches **Emoji | OwO whats this**
        val regex = Regex("(?:\\*\\*)?(.+?)(?:\\*\\*)?\\|(?:\\*\\*)?(.+)\\*\\*")

        private val logger = KotlinLogging.logger {}
    }

    /**
     * Title of the FAQ embed
     */
    abstract val title: String

    /**
     * Channel were the FAQ should be sent
     */
    abstract val channelId: Long

    fun start() = m.launch {
        while (true) {
            try {
                logger.info { "Updating FAQ Embeds in $channelId" }

                val channel = jda.getTextChannelById(channelId)

                if (channel != null) {
                    val allMessagesInTheChannel = channel.history.retrieveAllMessages()
                        .sortedBy { it.timeCreated }

                    logger.info { "There are ${allMessagesInTheChannel.size} messages in $channelId" }

                    val selfMessages = allMessagesInTheChannel.filter { it.author.idLong == jda.selfUser.idLong }
                    val otherMessages = allMessagesInTheChannel - selfMessages

                    val embeds = mutableListOf<MessageEmbed>()

                    var activeEmbed = EmbedBuilder()
                        .setTitle("${Emotes.LORI_COFFEE} $title")
                        .setColor(Color(114, 137, 218))

                    otherMessages.forEach {
                        var newText: String? = null

                        val embed = it.embeds.firstOrNull()
                        if (embed != null) {
                            newText = embed.title
                        } else {
                            val match = regex.find(it.contentRaw)

                            if (match != null) {
                                val emoji = match.groupValues[1].trim()
                                val text = match.groupValues[2].trim()

                                newText = "$emoji **|** [$text](${it.jumpUrl})\n"
                            }
                        }

                        if (newText != null) {
                            if (newText.length + activeEmbed.descriptionBuilder.length >= MessageEmbed.TEXT_MAX_LENGTH) {
                                embeds.add(activeEmbed.build())
                                activeEmbed = EmbedBuilder()
                                    // .setTitle("FAQ")
                                    .setColor(Color(114, 137, 218))
                            }

                            activeEmbed.appendDescription(newText)
                        }
                    }

                    embeds.add(activeEmbed.build())

                    var isDirty = embeds.size != selfMessages.size

                    if (!isDirty) {
                        for ((index, createdEmbed) in embeds.withIndex()) {
                            val selfEmbed = selfMessages.getOrNull(index)?.embeds?.firstOrNull()

                            if (selfEmbed == null) {
                                isDirty = true
                                break
                            } else {
                                // We replace \n to avoid the descriptions not matching, since Discord strips the \n if there isn't any content after it
                                if (selfEmbed.description?.replace(
                                        "\n",
                                        ""
                                    ) != createdEmbed.description?.replace("\n", "")
                                ) {
                                    isDirty = true
                                    break
                                }
                            }
                        }
                    }

                    if (isDirty) {
                        logger.info { "Ew, the messages are *dirty*! We are going to delete the old embeds and resend them again!" }
                        selfMessages.forEach {
                            it.delete().queue()
                        }

                        embeds.forEach {
                            channel.sendMessageEmbeds(it)
                                .queue()
                        }
                    }

                    logger.info { "Finished updating the messages!" }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            delay(60_000)
        }
    }
}