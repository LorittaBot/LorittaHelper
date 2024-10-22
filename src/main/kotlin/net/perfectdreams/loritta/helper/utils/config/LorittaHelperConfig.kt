package net.perfectdreams.loritta.helper.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class LorittaHelperConfig(
    val helper: InnerHelperConfig,
    val loritta: InnerLorittaConfig,
    val guilds: GuildHolderConfig,
    val tasks: TaskHolderConfig,
    val secretKey: String,
    val pantufaUrl: String? = null
) {
    @Serializable
    data class InnerHelperConfig(
        val token: String,
        val clientId: Long,
        val clientSecret: String,
        val database: DatabaseConfig? = null,
    )

    @Serializable
    data class InnerLorittaConfig(
        val token: String,
        val database: DatabaseConfig? = null,
        val api: LorittaAPIConfig
    ) {
        @Serializable
        data class LorittaAPIConfig(
            val url: String,
            val token: String
        )
    }

    @Serializable
    data class DatabaseConfig(
        val databaseName: String,
        val address: String,
        val username: String,
        val password: String
    )

    @Serializable
    data class GuildHolderConfig(
        val sparklyPower: SparklyPowerConfig,
        val community: CommunityConfig,
        val english: EnglishConfig
    ) {
        @Serializable
        data class SparklyPowerConfig(
            val id: Long,
            val channels: SparklyPowerChannelsConfig,
            val roles: SparklyPowerRolesConfig
        ) {
            @Serializable
            data class SparklyPowerChannelsConfig(
                val support: Long,
                val faq: Long,
                val status: Long
            )

            @Serializable
            data class SparklyPowerRolesConfig(
                val staff: Long
            )
        }

        @Serializable
        data class CommunityConfig(
            val id: Long,
            val channels: CommunityChannelsConfig,
            val roles: CommunityRolesConfig
        ) {
            @Serializable
            data class CommunityChannelsConfig(
                val support: Long,
                val faq: Long,
                val status: Long,
                val firstFanArt: Long,
                val firstFanArtRules: Long,
                val staff: Long,
                val staffFaq: Long,
                val serverReports: Long,
                val reportWarnings: Long,
                val saddestOfTheSads: Long,
                val openBar: Long,
                val sadCatsTribunal: Long,
                val reportsRelay: Long,
                val appeals: Long,
                val lorittaAutoMod: Long
            )

            @Serializable
            data class CommunityRolesConfig(
                val support: Long,
                val loriBodyguards: Long,
                val donator: Long,
                val superDonator: Long,
                val megaDonator: Long,
                val advertisement: Long,
                val firstFanArtManager: Long,
                val level10: Long,
                val drawing: Long
            )
        }

        @Serializable
        data class EnglishConfig(
            val id: Long,
            val channels: EnglishChannelsConfig,
            val roles: EnglishRolesConfig
        ) {
            @Serializable
            data class EnglishChannelsConfig(
                val support: Long,
                val status: Long,
                val faq: Long,
                val oldPortugueseSupport: Long,
                val oldEnglishSupport: Long,
                val otherBots: Long,
                val staff: Long
            )

            @Serializable
            data class EnglishRolesConfig(
                val englishSupport: Long,
                val portugueseSupport: Long
            )
        }
    }

    @Serializable
    data class TaskHolderConfig(
        val roleSynchronization: RoleSynchronizationConfig,
        val lorittaBannedRole: LorittaBannedRoleConfig
    ) {
        @Serializable
        data class RoleSynchronizationConfig(
            val enabled: Boolean,
            val rolesRemap: Map<String, Long>
        )

        @Serializable
        data class LorittaBannedRoleConfig(
            val enabled: Boolean,
            val guilds: List<LorittaGuildConfig>
        ) {
            @Serializable
            data class LorittaGuildConfig(
                val id: Long,
                val bannedRoleId: Long,
                val tempBannedRoleId: Long,
                val allowedChannels: List<Long>?
            )
        }
    }
}