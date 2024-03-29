package net.perfectdreams.discordinteraktions.common.modals

import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import net.perfectdreams.discordinteraktions.common.interactions.InteractionData
import net.perfectdreams.discordinteraktions.common.requests.RequestBridge

open class GuildModalContext(
    bridge: RequestBridge,
    sender: User,
    channelId: Snowflake,
    modalExecutorDeclaration: ModalExecutorDeclaration,
    dataOrNull: String?,
    data: InteractionData,
    discordInteractionData: DiscordInteraction,
    val guildId: Snowflake,
    val member: Member
) : ModalContext(bridge, sender, channelId, modalExecutorDeclaration, dataOrNull, data, discordInteractionData) {
    val appPermissions = discordInteractionData.appPermissions.value ?: error("App Permissions field is null on a Guild Interaction! Bug?")
}