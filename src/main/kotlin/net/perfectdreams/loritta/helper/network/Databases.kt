package net.perfectdreams.loritta.helper.network

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.sql.Database

class Databases(val m: LorittaHelper) {
    val lorittaDatabase by lazy {
        if (m.config.lorittaDatabase == null)
            throw RuntimeException("Accessing Loritta Database, but database is not configured!")

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:postgresql://${m.config.lorittaDatabase.address}/${m.config.lorittaDatabase.databaseName}"
        hikariConfig.username = m.config.lorittaDatabase.username
        hikariConfig.password = m.config.lorittaDatabase.password

        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }

    val helperDatabase by lazy {
        if (m.config.helperDatabase == null)
            throw RuntimeException("Accessing Helper Database, but database is not configured!")

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:postgresql://${m.config.helperDatabase.address}/${m.config.helperDatabase.databaseName}"
        hikariConfig.username = m.config.helperDatabase.username
        hikariConfig.password = m.config.helperDatabase.password

        val ds = HikariDataSource(hikariConfig)
        Database.connect(ds)
    }
}