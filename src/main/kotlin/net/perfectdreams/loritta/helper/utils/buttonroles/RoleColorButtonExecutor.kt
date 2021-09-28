package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.context.components.GuildComponentContext
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class RoleColorButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(RoleColorButtonExecutor::class, "role_color")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(data)

            if (!context.member.roles.contains(Snowflake(364201981016801281L))) {
                context.sendEphemeralMessage {
                    content = "Para você pegar uma cor personalizada, você precisa ser <@&364201981016801281>!"
                }
                return
            }

            val roleInformation = LorittaCommunityRoleButtons.colors.first { it.roleId == roleButtonData.roleId }

            if (roleButtonData.roleId in context.member.roles) {
                // Remove role
                m.helperRest.guild.deleteRoleFromGuildMember(
                    Snowflake(297732013006389252L),
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
                    Snowflake(297732013006389252L),
                    user.id,
                ) {
                    this.roles = context.member.roles.toMutableSet().apply {
                        this.removeAll(LorittaCommunityRoleButtons.colors.map { it.roleId })
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