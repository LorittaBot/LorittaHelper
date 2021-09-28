package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.builder.message.MessageBuilder

data class RoleButton(
    val label: String?,
    val roleId: Snowflake,
    val emoji: DiscordPartialEmoji,
    val description: String?,
    val messageReceive: MessageBuilder.(RoleButton) -> (Unit),
    val messageRemove: MessageBuilder.(RoleButton) -> (Unit)
)