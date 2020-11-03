package net.perfectdreams.loritta.helper.utils.config

import kotlinx.serialization.Serializable

@Serializable
class LorittaHelperConfig(
    val token: String,
    val githubToken: String,
    val lorittaDatabase: DatabaseConfig? = null
) {
    @Serializable
    class DatabaseConfig constructor(
        val databaseName: String,
        val address: String,
        val username: String,
        val password: String
    )
}