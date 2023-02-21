package net.perfectdreams.loritta.helper.utils.dontmentionstaff

import com.github.benmanes.caffeine.cache.Caffeine
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.util.concurrent.TimeUnit

/**
 * Replies to user that mentions a staff user instead of mentioning the support role
 */
abstract class DontMentionStaff {
    val sentMessageAt = Caffeine.newBuilder()
        .expireAfterAccess(7, TimeUnit.MINUTES)
        .build<Long, Long>()

    /**
     * ID of the support role
     */
    abstract val roleId: Long

    /**
     * Channel where the Don't Mention Staff feature is enabled
     */
    abstract val channelId: Long

    /**
     * Response that will be sent when someone mentions a staff
     */
    abstract fun getResponse(): List<LorittaReply>

    suspend fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.idLong == channelId) {
            // Please don't mention staff stuff
            sentMessageAt.put(event.author.idLong, System.currentTimeMillis())

            val supportMentioned =
                event.message.mentions.members.filter { it.roles.any { it.idLong == roleId } }

            for (support in supportMentioned) {
                val lastSentAt = sentMessageAt.getIfPresent(support.idLong)

                if (lastSentAt == null) {
                    // Don't mention staff grrr
                    val replies = getResponse()

                    event.channel.sendMessage(
                        MessageCreateBuilder()
                            .setAllowedMentions(listOf(Message.MentionType.USER, Message.MentionType.CHANNEL))
                            .setContent(replies.joinToString("\n", transform = { it.build(event) }))
                            .build()
                    ).setMessageReference(event.message).await()
                    return
                }
            }
        }
    }
}