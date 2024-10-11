package net.perfectdreams.loritta.helper.utils.extensions

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