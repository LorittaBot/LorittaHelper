package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.perfectdreams.galleryofdreams.common.FanArtTag

// Serial Names are present just to avoid a gigantic select menu payload
@Serializable
sealed class AddFanArtData {
    abstract val fanArtChannelId: Snowflake
    abstract val fanArtMessageId: Snowflake
    abstract val selectedAttachmentId: Snowflake?
    abstract val tags: List<FanArtTag>
}

@Serializable
@SerialName("add_existing")
data class AddFanArtToExistingArtistData(
    val artistId: Long,
    val artistSlug: String,
    override val fanArtChannelId: Snowflake,
    override val fanArtMessageId: Snowflake,
    override val selectedAttachmentId: Snowflake?,
    override val tags: List<FanArtTag>
) : AddFanArtData()

@Serializable
@SerialName("add_new")
data class AddFanArtToNewArtistData(
    val artistDiscordId: Snowflake,
    val artistName: String,
    val artistSlug: String,
    override val fanArtChannelId: Snowflake,
    override val fanArtMessageId: Snowflake,
    override val selectedAttachmentId: Snowflake?,
    override val tags: List<FanArtTag>
) : AddFanArtData()