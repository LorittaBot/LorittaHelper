package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.commands.SlashCommand
import net.perfectdreams.discordinteraktions.context.GuildSlashCommandContext
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.loritta.helper.LorittaHelper

abstract class HelperSlashCommand(
    val helper: LorittaHelper,
    declaration: SlashCommandDeclaration,
    parent: SlashCommandDeclaration = declaration
) : SlashCommand(declaration, parent) {
    override suspend fun executes(context: SlashCommandContext) {
        if (context !is GuildSlashCommandContext || !context.member.roles.contains(Snowflake(399301696892829706L))) {
            context.sendMessage {
                content = "Você não pode usar comandos da Helper!"

                flags = MessageFlags(MessageFlag.Ephemeral)
            }
            return
        }

        executesHelper(context)
    }

    abstract suspend fun executesHelper(context: SlashCommandContext)
}