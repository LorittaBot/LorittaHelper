package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.slash.SlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelper

abstract class HelperSlashExecutor(
    val helper: LorittaHelper
) : SlashCommandExecutor() {
    override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is GuildApplicationCommandContext || !context.member.roles.contains(Snowflake(399301696892829706L))) {
            context.sendEphemeralMessage {
                content = "Você não pode usar comandos da Helper!"
            }
            return
        }

        executeHelper(context, args)
    }

    abstract suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments)
}