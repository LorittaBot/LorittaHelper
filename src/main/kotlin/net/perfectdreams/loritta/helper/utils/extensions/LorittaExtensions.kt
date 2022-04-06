package net.perfectdreams.loritta.helper.utils.extensions

import net.dv8tion.jda.api.entities.User
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun User.getBannedState(m: LorittaHelper): ResultRow? {
    return transaction(m.databases.lorittaDatabase) {
        BannedUsers.select {
            BannedUsers.userId eq this@getBannedState.idLong and
                    (BannedUsers.valid eq true) and
                    (
                            BannedUsers.expiresAt.isNull()
                                    or
                                    (
                                            BannedUsers.expiresAt.isNotNull() and
                                                    (BannedUsers.expiresAt greaterEq System.currentTimeMillis()))
                            )
        }
                .orderBy(BannedUsers.bannedAt, SortOrder.DESC)
                .firstOrNull()
    }
}

fun User.isLorittaBanned(m: LorittaHelper): Boolean {
    getBannedState(m) ?: return false
    return true
}