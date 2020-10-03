package net.perfectdreams.loritta.helper.utils.faqembed

import kotlinx.coroutines.delay
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants
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
        val regex = Regex("(.+)\\*\\*\\|(?:\\*\\*)?(.+)\\*\\*")
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
                val channel = jda.getGuildById(Constants.SUPPORT_SERVER_ID)
                    ?.getTextChannelById(channelId)

                println("Channel: $channel")

                if (channel != null) {
                    println("Getting messages owo")

                    val allMessagesInTheChannel = channel.history.retrieveAllMessages()
                        .sortedBy { it.timeCreated }

                    val selfMessages = allMessagesInTheChannel.filter { it.author.idLong == jda.selfUser.idLong }
                    val otherMessages = allMessagesInTheChannel - selfMessages

                    val embeds = mutableListOf<MessageEmbed>()

                    var activeEmbed = EmbedBuilder()
                        .setTitle("${Emotes.LORI_COFFEE} $title")
                        .setColor(Color(114, 137, 218))

                    otherMessages.forEach {
                        println(it.contentRaw)
                        val match = regex.find(it.contentRaw)
                        println(match?.groupValues)

                        if (match != null) {
                            val emoji = match.groupValues[1].trim()
                            val text = match.groupValues[2].trim()

                            val newText = "$emoji **|** [$text](${it.jumpUrl})\n"

                            println(newText)

                            if (newText.length + activeEmbed.descriptionBuilder.length >= MessageEmbed.TEXT_MAX_LENGTH) {
                                embeds.add(activeEmbed.build())
                                activeEmbed = EmbedBuilder()
                                    // .setTitle("FAQ")
                                    .setColor(Color(114, 137, 218))
                            }

                            println(it.jumpUrl)
                            activeEmbed.appendDescription(newText)
                        }
                    }

                    embeds.add(activeEmbed.build())

                    var isDirty = embeds.size != selfMessages.size

                    if (!isDirty) {
                        for ((index, createdEmbed) in embeds.withIndex()) {
                            val selfEmbed = selfMessages.getOrNull(index)?.embeds?.firstOrNull()

                            if (selfEmbed == null) {
                                println("Embed is null")
                                isDirty = true
                                break
                            } else {
                                // We replace \n to avoid the descriptions not matching, since Discord strips the \n if there isn't any content after it
                                if (selfEmbed.description?.replace(
                                        "\n",
                                        ""
                                    ) != createdEmbed.description?.replace("\n", "")
                                ) {
                                    println("Description doesn't match")

                                    isDirty = true
                                    break
                                }
                            }
                        }
                    }

                    println("Is dirty? $isDirty")

                    if (isDirty) {
                        selfMessages.forEach {
                            it.delete().queue()
                        }

                        embeds.forEach {
                            channel.sendMessage(it)
                                .queue()
                        }
                    }

                    println("Finished")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            delay(60_000)
        }
    }
}