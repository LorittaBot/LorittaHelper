package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Position
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelperKord

class ServerMembersExecutor(helper: LorittaHelperKord, val rest: RestClient) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(ServerMembersExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            val guildId = string("guild_id", "ID do Servidor")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()

        val guildId = args[options.guildId]

        val builder = StringBuilder()

        var positionToBeChecked: Position? = Position.After(Snowflake(0))
        while (positionToBeChecked != null) {
            val members = rest.guild.getGuildMembers(Snowflake(guildId), limit = 1000, position = positionToBeChecked)

            // This *should* be in join order, I guess
            for (member in members) {
                val user = member.user.value

                builder.append("${user?.username}#${user?.discriminator} (${user?.id?.value}) [${member.joinedAt}]")
                builder.append("\n")
            }

            val maxIdInTheAllMembersList = members.maxByOrNull { it.user.value!!.id }

            positionToBeChecked = if (maxIdInTheAllMembersList != null) Position.After(maxIdInTheAllMembersList.user.value!!.id) else null
        }

        context.sendMessage {
            addFile("servers.txt", builder.toString().toByteArray(Charsets.UTF_8).inputStream())
        }
    }
}