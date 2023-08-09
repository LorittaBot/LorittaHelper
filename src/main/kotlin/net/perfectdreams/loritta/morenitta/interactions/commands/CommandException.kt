package net.perfectdreams.loritta.morenitta.interactions.commands

import dev.minn.jda.ktx.messages.InlineMessage

/**
 * Useful for command control flow, this allows you a quick and easy way to "halt" the execution of an command.
 *
 * Instead of showing the pre-defined generic error message, the [builder] message should be sent to the user.
 *
 * Implementations should catch this exception and send the [builder], logging the error is not required.
 */
class CommandException(val ephemeral: Boolean, val builder: InlineMessage<*>.() -> (Unit)) : RuntimeException()