package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.commands.get
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.required
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.sequins.text.StringUtils

class ServerMembersCommand(helper: LorittaHelper, val rest: RestClient) : HelperSlashCommand(helper, this) {
    companion object : SlashCommandDeclaration(
        name = "servermembers",
        description = "Mostra os membros de um servidor"
    ) {
        override val options = Options

        object Options : SlashCommandDeclaration.Options() {
            val guildId = string("guild_id", "ID do Servidor")
                .required()
                .register()

        }
    }

    override suspend fun executesHelper(context: SlashCommandContext) {
        val guildId = options.guildId.get(context)

        val members = rest.guild.getGuildMembers(Snowflake(guildId))

        val builder = StringBuilder()

        for (member in members) {
            val user = member.user.value

            builder.append("${user?.username}#${user?.discriminator} (${user?.id?.value})")
            builder.append("\n")
        }

        val split = StringUtils.chunkedLines(builder.toString(), 1900, true)

        context.sendMessage {
            content = "```\n" + split.first() + "\n```"
        }
    }
}