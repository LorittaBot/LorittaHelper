package net.perfectdreams.loritta.helper.utils.extensions

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.value
import dev.kord.core.entity.channel.TextChannel
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> RestAction<T>.await() : T {
    return suspendCoroutine { cont ->
        this.queue({ cont.resume(it)}, { cont.resumeWithException(it) })
    }
}

suspend fun MessageHistory.retrieveAllMessages(): List<Message> {
    val messages = mutableListOf<Message>()

    while (true) {
        val newMessages = this.retrievePast(100).await()
        if (newMessages.isEmpty())
            break

        messages += newMessages
    }

    return messages
}

fun DiscordPartialEmoji.toJDA(): CustomEmoji {
    return Emoji.fromCustom(this.name!!, this.id!!.value.toLong(), this.animated.value ?: false)
}