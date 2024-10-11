package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.PermissionLevel
import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.LorittaSlashCommandExecutor
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandArguments

abstract class HelperExecutor(
    val helper: LorittaHelper,
    val requiredPermissionLevel: PermissionLevel
) : LorittaSlashCommandExecutor() {
    companion object {
        val ADMIN_ROLES = listOf(
            333602159998271489L, // SparklyPower Admins
            693606685943660545L, // SparklyPower Coords
            333601725862641664L, // SparklyPower Owners
            351473717194522647,  // Loritta Bodyguards Community
            421325022951637015L, // Loritta Bodyguards Support
        )

        val HELPER_ROLES = listOf(
            399301696892829706L, // Support Community
            421325387889377291L, // Support BR Server
            332650495522897920L, // SparklyPower Staff
        )

        val FAN_ARTS_MANAGER_ROLES = listOf(
            924649809103691786L
        )
    }

    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val guild = context.guildOrNull

        if (guild == null) {
            context.reply(true) {
                content = "Você não pode usar comandos da Helper!"
            }
            return
        }

        val roleIds = context.member.roles.map { it.idLong }
        val permissionLevel = when {
            ADMIN_ROLES.any { it in roleIds } -> PermissionLevel.ADMIN
            HELPER_ROLES.any { it in roleIds } -> PermissionLevel.HELPER
            FAN_ARTS_MANAGER_ROLES.any { it in roleIds } -> PermissionLevel.FAN_ARTS_MANAGER
            else -> PermissionLevel.NOTHING
        }

        if (permissionLevel.ordinal > requiredPermissionLevel.ordinal) {
            context.reply(true) {
                content = "Você não pode usar comandos da Helper!"
            }
            return
        }

        executeHelper(context, args)
    }

    abstract suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments)
}