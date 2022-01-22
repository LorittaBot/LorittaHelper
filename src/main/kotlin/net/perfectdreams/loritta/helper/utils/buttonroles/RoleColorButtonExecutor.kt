package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.components.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.GuildComponentContext
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.LorittaLandGuild

class RoleColorButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(RoleColorButtonExecutor::class, "role_color")

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

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(data)
            val guildData = guildRolesData[roleButtonData.guild]!!

            if (!context.member.roles.any { it in guildData.allowedRoles }) {
                context.sendEphemeralMessage {
                    content = "Para você pegar uma cor personalizada, você precisa ser <@&364201981016801281>!"
                }
                return
            }

            val roleInformation = roleButtonData.guild.colors.first { it.roleId == roleButtonData.roleId }

            if (roleButtonData.roleId in context.member.roles) {
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
                    this.roles = context.member.roles.toMutableSet().apply {
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