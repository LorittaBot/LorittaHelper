package net.perfectdreams.loritta.helper.utils.dailycatcher.catchers

import kotlinx.coroutines.channels.Channel
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.cinnamon.pudding.tables.Dailies
import net.perfectdreams.loritta.cinnamon.pudding.tables.Profiles
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.SonhosPaymentReason
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherMessage
import net.perfectdreams.loritta.helper.utils.dailycatcher.ExecutedCommandsStats
import net.perfectdreams.loritta.helper.utils.dailycatcher.SonhosTransactionWrapper
import net.perfectdreams.loritta.helper.utils.dailycatcher.SuspiciousLevel
import net.perfectdreams.loritta.helper.utils.dailycatcher.UserDailyRewardCache
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

abstract class DailyCatcher<T>(val database: Database) {
    abstract suspend fun catch(channel: Channel<T>)
    abstract fun buildReportMessage(jda: JDA, bannedUsersIds: Set<Long>, report: T): DailyCatcherMessage

    fun retrieveSonhos(userId: Long): Long? = transaction(database) {
        Profiles.selectAll().where { Profiles.id eq userId }
            .firstOrNull()?.get(Profiles.money)
    }

    fun retrieveUserLastDailyReward(userId: Long) = transaction(database) {
        Dailies.selectAll().where { Dailies.receivedById eq userId }.orderBy(Dailies.receivedAt, SortOrder.DESC)
            .firstOrNull()
    }

    /**
     * Retrieves the executed commands stats for the [userId]
     *
     * @param userId the user's ID
     * @return the command stats
     */
    fun retrieveExecutedCommandsStats(userId: Long): ExecutedCommandsStats {
        val commandCountField = ExecutedCommandsLog.command.count()

        val commands = transaction(database) {
            ExecutedCommandsLog.select(ExecutedCommandsLog.command, commandCountField)
                .where { ExecutedCommandsLog.userId eq userId }
                .groupBy(ExecutedCommandsLog.command)
                .orderBy(commandCountField, SortOrder.DESC)
                .toList()
        }

        val cmdQuantity = commands.sumBy { it[commandCountField].toInt() }
        val cmdEconomyQuantity = commands.filter { it[ExecutedCommandsLog.command] in DailyCatcherManager.ECONOMY_COMMANDS }
                .sumBy { it[commandCountField].toInt() }
        val cmdEconomyLenientQuantity = commands.filter { it[ExecutedCommandsLog.command] in DailyCatcherManager.LENIENT_ECONOMY_COMMANDS }
                .sumBy { it[commandCountField].toInt() }
        return ExecutedCommandsStats(
                cmdQuantity,
                cmdEconomyQuantity,
                cmdEconomyLenientQuantity
        )
    }

    fun appendHeader(type: String, suspiciousLevel: SuspiciousLevel): String {
        var userInput = ""

        userInput = "⸻⸻⸻⸻⸻⸻⸻⸻⸻\n"
        userInput += "<:catpolice:585608392110899200> **DENÚNCIA DA POLÍCIA ESCARLATE**\n"
        userInput += "<:lori_what:626942886361038868> **Tipo:** $type\n"
        userInput += "${suspiciousLevel.emote} **Nível de sus:** *${suspiciousLevel.text}*\n\n"

        return userInput
    }

    fun appendMeta(ids: List<Long>) = "**Meta:** ||" + ids.joinToString(";") + "||"

    fun EmbedBuilder.appendDailyList(ids: List<Long>): EmbedBuilder {
        var input = ""

        val dailies = transaction(database) {
            SonhosTransaction.selectAll().where {
                SonhosTransaction.receivedBy inList ids and
                        (SonhosTransaction.reason eq SonhosPaymentReason.DAILY)
            }.orderBy(SonhosTransaction.givenAt, SortOrder.DESC).toList()
        }

        for (daily in dailies) {
            val addToInput = "`[${DailyCatcherManager.formatDate(daily[SonhosTransaction.givenAt])}]` `${daily[SonhosTransaction.receivedBy]}` - ${daily[SonhosTransaction.quantity].toLong()} sonhos\n"

            if (input.length + addToInput.length > 1000)
                break

            input += addToInput
        }

        this.addField("\uD83D\uDCDD Dailies", input, false)
        return this
    }

    fun EmbedBuilder.appendTransactionsToEmbed(
            transactions: List<SonhosTransactionWrapper>,
            dailyRewardCache: UserDailyRewardCache
    ): EmbedBuilder {
        var transactionFields = ""
        for (transaction in transactions.sortedByDescending { it.givenAt }) {
            val givenByEmail = dailyRewardCache.getOrRetrieveEmail(database, transaction.givenById)
            val receivedByEmail = dailyRewardCache.getOrRetrieveEmail(database, transaction.receivedById)

            val addToInput = "`[${DailyCatcherManager.formatDate(transaction.givenAt)}]` `${transaction.givenById}` (`$givenByEmail`) -> `${transaction.receivedById}` (`$receivedByEmail`) - ${transaction.quantity} sonhos\n"

            if (transactionFields.length + addToInput.length > 1000)
                break

            transactionFields += addToInput
        }

        this.addField("\uD83D\uDCB8 Transações Relacionadas", transactionFields, false)
        return this
    }

    fun convertToWrapper(result: ResultRow) = SonhosTransactionWrapper(
            result[SonhosTransaction.givenBy] ?: -1,
            result[SonhosTransaction.receivedBy] ?: -1,
            result[SonhosTransaction.quantity].toLong(),
            result[SonhosTransaction.givenAt]
    )
}