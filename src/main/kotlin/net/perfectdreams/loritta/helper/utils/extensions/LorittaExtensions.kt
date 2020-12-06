package net.perfectdreams.loritta.helper.utils.extensions

import net.dv8tion.jda.api.entities.User
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BannedUsers : LongIdTable() {
    val userId = long("user").index()
    val valid = bool("valid").index()
    val bannedAt = long("banned_at")
    val expiresAt = long("expires_at").nullable()
    val reason = text("reason")
    val bannedBy = long("banned_by").nullable()
}

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