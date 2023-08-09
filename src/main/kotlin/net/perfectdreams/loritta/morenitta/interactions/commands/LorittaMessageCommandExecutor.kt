package net.perfectdreams.loritta.morenitta.interactions.commands

import net.dv8tion.jda.api.entities.Message

abstract class LorittaMessageCommandExecutor {
    abstract suspend fun execute(context: ApplicationCommandContext, message: Message)
}