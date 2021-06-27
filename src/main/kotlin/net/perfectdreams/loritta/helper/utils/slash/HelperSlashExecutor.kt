package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.api.entities.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.context.commands.GuildSlashCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.loritta.helper.LorittaHelper

abstract class HelperSlashExecutor(
    val helper: LorittaHelper
) : SlashCommandExecutor() {
    override suspend fun execute(context: SlashCommandContext, args: SlashCommandArguments) {
        if (context !is GuildSlashCommandContext || !context.member.roles.contains(Snowflake(351473717194522647L))) {
            context.sendMessage {
                content = "Você não pode usar comandos da Helper!"

                isEphemeral = true
            }
            return
        }

        executeHelper(context, args)
    }

    abstract suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments)
}