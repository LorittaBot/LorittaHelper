package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.context.components.GuildComponentContext
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class RoleButtonExecutor(val m: LorittaHelper) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(RoleButtonExecutor::class, "role_button")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // This can only happen in a guild... right? I hope so.
        if (context is GuildComponentContext) {
            val roleButtonData = ComponentDataUtils.decode<RoleButtonData>(data)

            if (roleButtonData.roleId in context.member.roles) {
                // Remove role
                m.helperRest.guild.deleteRoleFromGuildMember(
                    Snowflake(297732013006389252L),
                    user.id,
                    roleButtonData.roleId,
                    "Loritta Helper's Button Role Manager, yay!"
                )

                context.sendEphemeralMessage {
                    content = "Cargo removido com sucesso... sad"
                }
            } else {
                // Add role
                m.helperRest.guild.addRoleToGuildMember(
                    Snowflake(297732013006389252L),
                    user.id,
                    roleButtonData.roleId,
                    "Loritta Helper's Button Role Manager, yay!"
                )

                context.sendEphemeralMessage {
                    content = "Cargo recebido com sucesso!"
                }
            }
        }
    }
}