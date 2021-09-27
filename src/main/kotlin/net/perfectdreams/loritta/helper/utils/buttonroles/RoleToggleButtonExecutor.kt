package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.context.components.GuildComponentContext
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class RoleToggleButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(RoleToggleButtonExecutor::class, "role_toggle")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(data)

            val roleInformation = RoleButtons.notifications.first { it.roleId == roleButtonData.roleId }

            if (roleButtonData.roleId in context.member.roles) {
                // Remove role
                m.helperRest.guild.deleteRoleFromGuildMember(
                    Snowflake(297732013006389252L),
                    user.id,
                    roleButtonData.roleId,
                    RoleButtons.AUDIT_LOG_REASON
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
                    Snowflake(297732013006389252L),
                    user.id,
                    roleButtonData.roleId,
                    RoleButtons.AUDIT_LOG_REASON
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