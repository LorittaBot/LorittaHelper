package net.perfectdreams.loritta.morenitta.interactions.commands

import net.dv8tion.jda.api.entities.User

abstract class LorittaUserCommandExecutor {
    abstract suspend fun execute(context: ApplicationCommandContext, user: User)
}