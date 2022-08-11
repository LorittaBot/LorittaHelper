package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutor
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.GuildComponentContext
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.LorittaLandGuild

class RoleColorButtonExecutor(val m: LorittaHelperKord) : ButtonExecutor {
    companion object : ButtonExecutorDeclaration("role_color")

    val guildRolesData = mapOf(
        LorittaLandGuild.LORITTA_COMMUNITY to GuildRolesData(
            Snowflake(297732013006389252L),
            listOf(Snowflake(364201981016801281L))
        ),
        LorittaLandGuild.SPARKLYPOWER to GuildRolesData(
            Snowflake(320248230917046282L),
            listOf(Snowflake(332652664544428044L))
        )
    )

    override suspend fun onClick(user: User, context: ComponentContext) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(context.data)
            val guildData = guildRolesData[roleButtonData.guild]!!

            if (!context.member.roleIds.any { it in guildData.allowedRoles }) {
                context.sendEphemeralMessage {
                    content = "Para você pegar uma cor personalizada, você precisa ser ${guildData.allowedRoles.joinToString(" ou ") { "<@&${it.value}>" }}!"
                }
                return
            }

            val roleInformation = roleButtonData.guild.colors.first { it.roleId == roleButtonData.roleId }

            if (roleButtonData.roleId in context.member.roleIds) {
                // Remove role
                m.helperRest.guild.deleteRoleFromGuildMember(
                    guildData.guildId,
                    user.id,
                    roleButtonData.roleId,
                    LorittaCommunityRoleButtons.AUDIT_LOG_REASON
                )

                context.sendEphemeralMessage {
                    roleInformation.messageRemove.invoke(
                        this,
                        roleInformation
                    )
                }
            } else {
                // Add role
                // We use modifyGuildMember because we want to remove other badges that the user may have
                m.helperRest.guild.modifyGuildMember(
                    guildData.guildId,
                    user.id,
                ) {
                    this.roles = context.member.roleIds.toMutableSet().apply {
                        this.removeAll(roleButtonData.guild.colors.map { it.roleId }.toSet())
                        this.add(roleInformation.roleId)
                    }

                    this.reason = LorittaCommunityRoleButtons.AUDIT_LOG_REASON
                }

                context.sendEphemeralMessage {
                    roleInformation.messageReceive.invoke(
                        this,
                        roleInformation
                    )
                }
            }
        }
    }
}