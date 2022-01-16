package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.slash.SlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord

abstract class HelperSlashExecutor(
    val helper: LorittaHelperKord
) : SlashCommandExecutor() {
    companion object {
        private val ALLOWED_ROLES = listOf(
            Snowflake(399301696892829706L), // Support Community
            Snowflake(421325387889377291L), // Support BR Server
            Snowflake(693606685943660545L) // SparklyPower Coords
        )
    }
    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is GuildApplicationCommandContext || !context.member.roles.any { it in ALLOWED_ROLES }) {
            context.sendEphemeralMessage {
                content = "Você não pode usar comandos da Helper!"
            }
            return
        }

        executeHelper(context, args)
    }

    abstract suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments)
}