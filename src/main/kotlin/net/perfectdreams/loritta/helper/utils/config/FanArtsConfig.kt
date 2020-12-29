package net.perfectdreams.loritta.helper.utils.config

import kotlinx.serialization.Serializable

@Serializable
class FanArtsConfig constructor(
        val channels: Array<Long>,
        val emoteId: Long,
        val approveFanArtsRoleId: Long,
        val fanArtArtistsFolder: String,
        val fanArtFilesFolder: String,
        val placesToPlaceStuff: List<String>,
        val fanArtsChannelId: Long,
        val firstFanArtRoleId: Long,
        val firstFanArtChannelId: Long
)