package net.perfectdreams.loritta.morenitta.interactions

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.InlineMessage
import dev.minn.jda.ktx.messages.MessageCreate
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.morenitta.interactions.commands.CommandException

abstract class InteractionContext(
    val loritta: LorittaHelper
) {
    abstract val event: IReplyCallback
    val guildId
        get() = event.guild?.idLong

    val guildOrNull: Guild?
        get() = event.guild

    val guild: Guild
        get() = guildOrNull ?: error("This interaction was not sent in a guild!")

    val user
        get() = event.user

    val memberOrNull: Member?
        get() = event.member

    val member: Member
        get() = memberOrNull ?: error("This interaction was not sent in a guild!")


    var wasInitiallyDeferredEphemerally: Boolean? = null

    suspend fun deferChannelMessage(ephemeral: Boolean): InteractionHook {
        val hook = event.deferReply().setEphemeral(ephemeral).await()
        wasInitiallyDeferredEphemerally = ephemeral
        return hook
    }

    suspend inline fun reply(ephemeral: Boolean, content: String) = reply(ephemeral) {
        this.content = content
    }

    suspend inline fun reply(ephemeral: Boolean, builder: InlineMessage<MessageCreateData>.() -> Unit = {}): InteractionMessage {
        val createdMessage = InlineMessage(MessageCreateBuilder()).apply(builder).build()

        // We could actually disable the components when their state expires, however this is hard to track due to "@original" or ephemeral messages not having an ID associated with it
        // So, if the message is edited, we don't know if we *can* disable the components when their state expires!

        return if (event.isAcknowledged) {
            val message = event.hook.sendMessage(createdMessage).setEphemeral(ephemeral).await()
            InteractionMessage.FollowUpInteractionMessage(message)
        } else {
            val hook = event.reply(createdMessage).setEphemeral(ephemeral).await()
            wasInitiallyDeferredEphemerally = ephemeral
            InteractionMessage.InitialInteractionMessage(hook)
        }
    }

    suspend fun sendMessage(channelId: Long, block: suspend InlineMessage<*>.() -> Unit): Message? {
        val builtMessage = MessageCreate {
            block()
        }

        return this.loritta.jda.getTextChannelById(channelId)?.sendMessage(builtMessage)?.await()
    }

    /**
     * Throws a [CommandException] with a specific message [block], halting command execution
     *
     * @param reply the message that will be sent
     * @see fail
     * @see CommandException
     */
    fun fail(ephemeral: Boolean, text: String, emote: String): Nothing = throw CommandException(ephemeral) {
        styled(text, emote)
    }

    /**
     * Throws a [CommandException] with a specific message [block], halting command execution
     *
     * @param reply the message that will be sent
     * @see fail
     * @see CommandException
     */
    fun fail(ephemeral: Boolean, builder: InlineMessage<*>.() -> Unit = {}): Nothing = throw CommandException(ephemeral) {
        builder()
    }
}