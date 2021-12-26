package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import net.perfectdreams.galleryofdreams.common.FanArtTag

@Serializable
data class AddFanArtData(
    val fanArtChannelId: Snowflake,
    val fanArtMessageId: Snowflake,
    val selectedAttachmentId: Snowflake?,
    val tags: List<FanArtTag>
)