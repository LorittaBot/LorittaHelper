package net.perfectdreams.loritta.helper.utils.extensions

import net.dv8tion.jda.api.entities.User
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BannedUsers : LongIdTable() {
    val userId = long("user").index()
    val valid = bool("valid").index()
    val bannedAt = long("banned_at")
    val expiresAt = long("expires_at").nullable()
    val reason = text("reason")
    val bannedBy = long("banned_by").nullable()
}

fun User.isLorittaBanned(m: LorittaHelper): Boolean {
    val bannedState = transaction(m.databases.lorittaDatabase) {
        BannedUsers.select { BannedUsers.userId eq this@isLorittaBanned.idLong }
            .orderBy(BannedUsers.bannedAt, SortOrder.DESC)
            .firstOrNull()
    } ?: return false

    if (bannedState[BannedUsers.valid]
        && bannedState[BannedUsers.expiresAt]
        ?: Long.MAX_VALUE >= System.currentTimeMillis())
        return true

    return false
}