package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord

abstract class HelperSlashExecutor(
    val helper: LorittaHelperKord,
    val requiredPermissionLevel: PermissionLevel
) : SlashCommandExecutor() {
    companion object {
        private val ADMIN_ROLES = listOf(
            Snowflake(693606685943660545L), // SparklyPower Coords
            Snowflake(333601725862641664L), // SparklyPower Owners
        )

        private val HELPER_ROLES = listOf(
            Snowflake(399301696892829706L), // Support Community
            Snowflake(421325387889377291L), // Support BR Server
        )
    }

    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is GuildApplicationCommandContext) {
            context.sendEphemeralMessage {
                content = "Você não pode usar comandos da Helper!"
            }
            return
        }

        val permissionLevel = when {
            ADMIN_ROLES.any { it in context.member.roles } -> PermissionLevel.ADMIN
            HELPER_ROLES.any { it in context.member.roles } -> PermissionLevel.HELPER
            else -> PermissionLevel.NOTHING
        }

        if (permissionLevel.ordinal > requiredPermissionLevel.ordinal) {
            context.sendEphemeralMessage {
                content = "Você não pode usar comandos da Helper!"
            }
            return
        }

        executeHelper(context, args)
    }

    abstract suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments)
}