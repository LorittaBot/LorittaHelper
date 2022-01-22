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

class RoleToggleButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(RoleToggleButtonExecutor::class, "role_toggle")

    val guildRolesData = mapOf(
        LorittaLandGuild.LORITTA_COMMUNITY to GuildRolesData(
            Snowflake(297732013006389252L),
            listOf()
        ),
        LorittaLandGuild.SPARKLYPOWER to GuildRolesData(
            Snowflake(320248230917046282L),
            listOf()
        )
    )

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(data)
            val guildData = guildRolesData[roleButtonData.guild]!!

            val roleInformation = roleButtonData.guild.notifications.first { it.roleId == roleButtonData.roleId }

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
                m.helperRest.guild.addRoleToGuildMember(
                    guildData.guildId,
                    user.id,
                    roleButtonData.roleId,
                    LorittaCommunityRoleButtons.AUDIT_LOG_REASON
                )

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