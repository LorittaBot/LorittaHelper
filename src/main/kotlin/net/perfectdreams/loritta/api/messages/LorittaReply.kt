package net.perfectdreams.loritta.api.messages

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class LorittaReply(
        val message: String = " ",
        val prefix: String? = null,
        val forceMention: Boolean = false,
        val hasPadding: Boolean = true,
        val mentionUser: Boolean = true
) {
    fun build(event: MessageReceivedEvent): String {
        return build(event.author.asMention, event.author.asMention + " ")
    }

    fun build(user: User) = build(user.asMention, null)

    fun build() = build(null, null)

    fun build(mention: String? = null, contextAsMention: String? = null): String {
        var send = ""
        if (prefix != null) {
            send = "$prefix **|** "
        } else if (hasPadding) {
            send = "$LEFT_PADDING **|** "
        }
        if (mentionUser && mention != null) {
            send = if (forceMention || contextAsMention == null) {
                "$send$mention "
            } else {
                send + contextAsMention
            }
        }
        send += message
        return send
    }

    companion object {
        private const val LEFT_PADDING = "\uD83D\uDD39"
    }
}