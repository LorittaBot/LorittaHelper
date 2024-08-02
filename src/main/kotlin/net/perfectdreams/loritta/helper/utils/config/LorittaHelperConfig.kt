package net.perfectdreams.loritta.helper.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class LorittaHelperConfig(
    val helper: InnerHelperConfig,
    val loritta: InnerLorittaConfig,
    val secretKey: String,
    val pantufaUrl: String? = null
) {
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
    data class InnerHelperConfig(
        val token: String,
        val clientId: Long,
        val clientSecret: String,
        val database: DatabaseConfig? = null,
    )

    @Serializable
    data class DatabaseConfig(
        val databaseName: String,
        val address: String,
        val username: String,
        val password: String
    )
}