package net.perfectdreams.loritta.helper.network

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.sql.Database

class Databases(val m: LorittaHelper) {
    val lorittaDatabase by lazy {
        if (m.config.loritta.database == null)
            throw RuntimeException("Accessing Loritta Database, but database is not configured!")

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:postgresql://${m.config.loritta.database.address}/${m.config.loritta.database.databaseName}"
        hikariConfig.username = m.config.loritta.database.username
        hikariConfig.password = m.config.loritta.database.password

        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }

    val helperDatabase by lazy {
        if (m.config.helper.database == null)
            throw RuntimeException("Accessing Helper Database, but database is not configured!")

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:postgresql://${m.config.helper.database.address}/${m.config.helper.database.databaseName}"
        hikariConfig.username = m.config.helper.database.username
        hikariConfig.password = m.config.helper.database.password

        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }
}