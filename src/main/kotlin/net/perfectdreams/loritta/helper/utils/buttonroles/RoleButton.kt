package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.minn.jda.ktx.messages.InlineMessage
import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji

data class RoleButton(
    val label: String?,
    val roleId: Long,
    val emoji: CustomEmoji,
    val description: String?,
    val messageReceive: InlineMessage<*>.(RoleButton) -> (Unit),
    val messageRemove: InlineMessage<*>.(RoleButton) -> (Unit)
)