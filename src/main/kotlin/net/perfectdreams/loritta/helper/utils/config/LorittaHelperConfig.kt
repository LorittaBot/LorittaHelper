package net.perfectdreams.loritta.helper.utils.config

import kotlinx.serialization.Serializable

@Serializable
class LorittaHelperConfig(
    val applicationId: Long,
    val token: String,
    val secretKey: String,
    val lorittaDatabase: DatabaseConfig? = null,
    val helperDatabase: DatabaseConfig? = null,
    val pantufaUrl: String? = null,
    val lorittaApi: LorittaAPIConfig
) {
    @Serializable
    class DatabaseConfig(
        val databaseName: String,
        val address: String,
        val username: String,
        val password: String
    )

    @Serializable
    class LorittaAPIConfig(
        val url: String,
        val token: String
    )
}