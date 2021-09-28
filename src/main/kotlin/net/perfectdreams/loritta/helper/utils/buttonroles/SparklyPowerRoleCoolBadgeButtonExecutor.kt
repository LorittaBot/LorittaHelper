package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.context.components.GuildComponentContext
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class SparklyPowerRoleCoolBadgeButtonExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(SparklyPowerRoleCoolBadgeButtonExecutor::class, "spk_role_badge")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(data)

            if (!context.member.roles.contains(Snowflake(332652664544428044L)) && !context.member.roles.contains(Snowflake(834625069321551892L))) {
                context.sendEphemeralMessage {
                    content = "Para você pegar um ícone personalizado, você precisa ser <@&332652664544428044> ou <@&834625069321551892>!"
                }
                return
            }

            val roleInformation = SparklyPowerRoleButtons.coolBadges.first { it.roleId == roleButtonData.roleId }

            if (roleButtonData.roleId in context.member.roles) {
                // Remove role
                m.helperRest.guild.deleteRoleFromGuildMember(
                    Snowflake(320248230917046282L),
                    user.id,
                    roleButtonData.roleId,
                    SparklyPowerRoleButtons.AUDIT_LOG_REASON
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
                    Snowflake(320248230917046282L),
                    user.id,
                ) {
                    this.roles = context.member.roles.toMutableSet().apply {
                        this.removeAll(SparklyPowerRoleButtons.coolBadges.map { it.roleId })
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