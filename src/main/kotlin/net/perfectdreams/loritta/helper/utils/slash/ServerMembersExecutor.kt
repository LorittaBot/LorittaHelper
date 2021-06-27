package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.sequins.text.StringUtils

class ServerMembersExecutor(helper: LorittaHelper, val rest: RestClient) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(ServerMembersExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            val guildId = string("guild_id", "ID do Servidor")
                .register()
        }
    }

    override suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments) {
        context.defer()
        val guildId = args[options.guildId]

        val members = rest.guild.getGuildMembers(Snowflake(guildId), limit = 1000)

        val builder = StringBuilder()

        for (member in members.sortedBy { it.joinedAt }) {
            val user = member.user.value

            builder.append("${user?.username}#${user?.discriminator} (${user?.id?.value}) [${member.joinedAt}]")
            builder.append("\n")
        }

        val split = StringUtils.chunkedLines(builder.toString(), 1980, true)

        for (text in split) {
            context.sendMessage {
                content = "```\n$text\n```"
            }
        }
    }
}