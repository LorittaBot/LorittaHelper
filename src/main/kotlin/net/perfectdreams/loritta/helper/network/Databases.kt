package net.perfectdreams.loritta.helper.network

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.sql.Database

class Databases(val m: LorittaHelper) {
    val lorittaDatabase by lazy {
        if (m.helperConfig.loritta.database == null)
            throw RuntimeException("Accessing Loritta Database, but database is not configured!")

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:postgresql://${m.helperConfig.loritta.database.address}/${m.helperConfig.loritta.database.databaseName}"
        hikariConfig.username = m.helperConfig.loritta.database.username
        hikariConfig.password = m.helperConfig.loritta.database.password

        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }

    val helperDatabase by lazy {
        if (m.helperConfig.helper.database == null)
            throw RuntimeException("Accessing Helper Database, but database is not configured!")

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:postgresql://${m.helperConfig.helper.database.address}/${m.helperConfig.helper.database.databaseName}"
        hikariConfig.username = m.helperConfig.helper.database.username
        hikariConfig.password = m.helperConfig.helper.database.password

        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }
}