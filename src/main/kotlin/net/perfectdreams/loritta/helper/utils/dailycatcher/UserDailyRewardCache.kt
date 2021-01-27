package net.perfectdreams.loritta.helper.utils.dailycatcher

import net.perfectdreams.loritta.helper.tables.Dailies
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserDailyRewardCache {
    private val cachedLastDailies = mutableMapOf<Long, ResultRow?>()

    fun getOrRetrieveIP(database: Database, userId: Long) = getOrRetrieveUserLastDailyReward(database, userId)?.getOrNull(Dailies.ip) ?: "???"
    fun getOrRetrieveEmail(database: Database, userId: Long) = getOrRetrieveUserLastDailyReward(database, userId)?.getOrNull(Dailies.email) ?: "???"

    fun getOrRetrieveUserLastDailyReward(database: Database, userId: Long) = cachedLastDailies.getOrPut(userId) {
        retrieveUserLastDailyReward(database, userId)
    }

    fun retrieveUserLastDailyReward(database: Database, userId: Long) = transaction(database) {
        Dailies.select {
            Dailies.receivedById eq userId
        }.orderBy(Dailies.receivedAt, SortOrder.DESC)
                .firstOrNull()
    }
}