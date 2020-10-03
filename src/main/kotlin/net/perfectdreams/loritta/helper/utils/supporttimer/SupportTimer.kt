package net.perfectdreams.loritta.helper.utils.supporttimer

import kotlinx.coroutines.delay
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.extensions.await

/**
 * Creates a timer in the specified [channelId], automatically sending the message if the last sent message was after 5 minutes
 * The message is not sent if the last message was the timer itself.
 */
abstract class SupportTimer(val m: LorittaHelper, val jda: JDA) {
    /**
     * The ID of the channel were the timer is active
     */
    abstract val channelId: Long

    /**
     * What replies should be sent when the timer is triggered
     */
    abstract val replies: List<LorittaReply>

    /**
     * Used when the last message was by the helper bot, if the last message contains this string, the timer won't be triggered
     */
    abstract val containsString: String

    fun start() = m.launch {
        while (true) {
            val channel = jda.getGuildById(Constants.SUPPORT_SERVER_ID)
                ?.getTextChannelById(channelId)

            if (channel != null) {
                val lastSentMessage = channel.history.retrievePast(1)
                    .await()
                    .firstOrNull()

                var resend = true
                if (lastSentMessage != null) {
                    resend = if (lastSentMessage.author.idLong == jda.selfUser.idLong && lastSentMessage.contentRaw.contains(containsString)) {
                        false
                    } else {
                        // last message check
                        val diff = System.currentTimeMillis() - (lastSentMessage.timeCreated.toEpochSecond() * 1000)

                        diff >= 300000L // Only resend after five minutes since the last message
                    }
                }

                if (resend) {
                    channel.sendMessage(
                        MessageBuilder()
                            .setAllowedMentions(listOf(Message.MentionType.USER, Message.MentionType.CHANNEL, Message.MentionType.EMOTE))
                            .setContent(replies.joinToString("\n", transform = { it.build() } ))
                            .build()
                    ).await()
                }
            }

            delay(60_000)
        }
    }
}