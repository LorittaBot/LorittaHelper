package net.perfectdreams.loritta.helper.utils.dailycatcher

import net.perfectdreams.loritta.helper.tables.Dailies
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.SonhosPaymentReason
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DailySameIpCatcher(val database: Database) {
    fun catch(): MutableList<ReportSameIpSuspicious> {
        val reports = mutableListOf<ReportSameIpSuspicious>()

        val dailyCount = Dailies.ip.count()
        val t = transaction(database) {
            val sameIpDailyList = Dailies.slice(Dailies.ip, dailyCount).select {
                Dailies.receivedAt greaterEq DailyCatcher.todayAtMidnight()
            }.groupBy(Dailies.ip)
                .orderBy(dailyCount, SortOrder.DESC)
                // Only check users with daily >= 2
                .filter { it[dailyCount] >= 2 }

            for ((index, ip) in sameIpDailyList.map { it[Dailies.ip] }.withIndex()) {
                if (index % 100 == 0)
                    println("$index/${sameIpDailyList.size} - Reports: ${reports.size}")

                val users = mutableListOf<UserAndEmail>()

                val checkedByIpDailyList = Dailies.select {
                    Dailies.receivedAt greaterEq DailyCatcher.todayAtMidnight() and (Dailies.ip eq ip)
                }.toList()
                    .distinctBy { it[Dailies.receivedById] }

                // Only one... why? idk, let's just continue and be happy...
                if (checkedByIpDailyList.size == 1)
                    continue

                val userIds = checkedByIpDailyList.map { it[Dailies.receivedById] }

                val alsoGivenTransactions = SonhosTransaction.select {
                    (SonhosTransaction.receivedBy inList userIds).and(SonhosTransaction.givenBy inList userIds)
                        .and(SonhosTransaction.reason eq SonhosPaymentReason.PAYMENT).and(SonhosTransaction.givenAt greaterEq DailyCatcher.fourteenDaysAgo())
                }.orderBy(SonhosTransaction.givenAt, SortOrder.DESC)

                checkedByIpDailyList.forEach {
                    users.add(
                        UserAndEmail(
                            it[Dailies.receivedById],
                            it[Dailies.ip],
                            it[Dailies.email],
                            it[Dailies.receivedAt]
                        )
                    )
                }

                reports.add(
                    ReportSameIpSuspicious(
                        users,
                        alsoGivenTransactions.map {
                            SonhosTransactionWrapper(
                                it[SonhosTransaction.givenBy] ?: -1,
                                it[SonhosTransaction.receivedBy] ?: -1,
                                it[SonhosTransaction.quantity].toLong(),
                                it[SonhosTransaction.givenAt]
                            )
                        }
                    )
                )
            }
        }

        return reports
    }
}