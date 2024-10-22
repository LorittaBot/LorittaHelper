package net.perfectdreams.loritta.helper.utils.extensions

import net.dv8tion.jda.api.entities.User
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction

fun User.getBannedState(m: LorittaHelper): ResultRow? {
    return transaction(m.databases.lorittaDatabase) {
        BannedUsers.selectAll().where {
            BannedUsers.userId eq idLong and
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