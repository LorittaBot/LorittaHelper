package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.DiscordGuildMember
import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Position
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord

class ServerMembersExecutor(helper: LorittaHelperKord, val rest: RestClient) : HelperSlashExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val guildId = string("guild_id", "ID do Servidor")

        val sortType = string("sort", "Organizar lista por...") {
            choice("Quando a conta foi criada", "created_at")
            choice("Quando a conta entrou no servidor", "joined_at")
        }
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()

        val sortType = args[options.sortType]
        val guildId = args[options.guildId]

        val builder = StringBuilder()

        val allMembers = mutableListOf<DiscordGuildMember>()

        var positionToBeChecked: Position.After? = Position.After(Snowflake(0))
        while (positionToBeChecked != null) {
            val members = rest.guild.getGuildMembers(Snowflake(guildId), limit = 1000, after = positionToBeChecked)
            allMembers.addAll(members)
            val maxIdInTheAllMembersList = members.maxByOrNull { it.user.value!!.id }
            positionToBeChecked = if (maxIdInTheAllMembersList != null) Position.After(maxIdInTheAllMembersList.user.value!!.id) else null
        }

        // This *should* be in join order, I guess
        for (member in allMembers.sortedBy {
            if (sortType == "created_at")
                it.user.value!!.id.timestamp
            else
                it.joinedAt
        }) {
            val user = member.user.value!!

            builder.append("${user.username}#${user.discriminator} (${user.id.value}) <${user.id.timestamp}> [${member.joinedAt}]")
            builder.append("\n")
        }

        context.sendMessage {
            addFile("servers.txt", builder.toString().toByteArray(Charsets.UTF_8).inputStream())
        }
    }
}