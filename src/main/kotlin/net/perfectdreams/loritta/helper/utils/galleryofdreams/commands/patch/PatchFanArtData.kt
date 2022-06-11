package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch

import kotlinx.serialization.Serializable
import net.perfectdreams.galleryofdreams.common.FanArtTag

@Serializable
data class PatchFanArtData(
    val fanArtSlug: String,
    val tags: List<FanArtTag>
)