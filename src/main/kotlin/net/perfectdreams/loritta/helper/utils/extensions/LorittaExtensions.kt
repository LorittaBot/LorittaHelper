package net.perfectdreams.loritta.helper.utils.extensions

import net.dv8tion.jda.api.entities.User
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.BannedUsers
import org.jetbrains.exposed.sql.*
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