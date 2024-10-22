package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

class LorittaBanTimeoutListener(val m: LorittaHelper): ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        // On any server that Loritta Helper is in...
        if (isLorittaBanned(m, event.author.idLong)) {
            event.guild.timeoutFor(event.author, Duration.ofDays(28))
                .reason("User is Loritta Banned!")
                .queue()

            event.message.delete()
                .reason("User is Loritta Banned!")
        }
    }

    private fun getBannedState(m: LorittaHelper, userId: Long): ResultRow? {
        return transaction(m.databases.lorittaDatabase) {
            BannedUsers.selectAll().where {
                BannedUsers.userId eq userId and
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

    private fun isLorittaBanned(m: LorittaHelper, userId: Long): Boolean {
        getBannedState(m, userId) ?: return false
        return true
    }
}