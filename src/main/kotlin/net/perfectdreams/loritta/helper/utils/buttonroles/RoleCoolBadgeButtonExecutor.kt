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

class RoleCoolBadgeButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(RoleCoolBadgeButtonExecutor::class, "role_badge")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(data)

            if (!context.member.roles.contains(Snowflake(364201981016801281L)) && !context.member.roles.contains(Snowflake(655132411566358548L))) {
                context.sendEphemeralMessage {
                    content = "Para você pegar um ícone personalizado, você precisa ser <@&364201981016801281> ou <@&655132411566358548>!"
                }
                return
            }

            val roleInformation = RoleButtons.coolBadges.first { it.roleId == roleButtonData.roleId }

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
                // We use modifyGuildMember because we want to remove other badges that the user may have
                m.helperRest.guild.modifyGuildMember(
                    Snowflake(297732013006389252L),
                    user.id,
                ) {
                    this.roles = context.member.roles.toMutableSet().apply {
                        this.removeAll(RoleButtons.coolBadges.map { it.roleId })
                        this.add(roleInformation.roleId)
                    }

                    this.reason = RoleButtons.AUDIT_LOG_REASON
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