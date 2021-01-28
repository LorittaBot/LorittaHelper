package net.perfectdreams.loritta.helper.utils.dailycatcher.catchers

import kotlinx.coroutines.channels.Channel
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.perfectdreams.loritta.helper.tables.Dailies
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherMessage
import net.perfectdreams.loritta.helper.utils.dailycatcher.ExecutedCommandsStats
import net.perfectdreams.loritta.helper.utils.dailycatcher.SuspiciousLevel
import net.perfectdreams.loritta.helper.utils.dailycatcher.UserDailyRewardCache
import net.perfectdreams.loritta.helper.utils.dailycatcher.UserInfoCache
import net.perfectdreams.loritta.helper.utils.dailycatcher.reports.ReportOnlyEcoCatcher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.time.Instant

class DailyOnlyEcoCommandsCatcher(database: Database) : DailyCatcher<ReportOnlyEcoCatcher>(database) {
    companion object {
        private const val ECONOMY_COMMANDS_THRESHOLD = 0.95
        private const val LENIENT_ECONOMY_COMMANDS_THRESHOLD = 0.98
        private const val CHUNKED_EXECUTED_COMMAND_LOGS_COUNT = 500
        private val logger = KotlinLogging.logger {}
    }

    override suspend fun catch(channel: Channel<ReportOnlyEcoCatcher>) {
        val dailies = transaction(Connection.TRANSACTION_READ_UNCOMMITTED, 5, database) {
            Dailies.select {
                Dailies.receivedAt greaterEq DailyCatcherManager.yesterdayAtMidnight() and (Dailies.receivedAt lessEq DailyCatcherManager.yesterdayBeforeDaySwitch())
            }.toList()
        }

        // bulk stats
        val commandCountField = ExecutedCommandsLog.command.count()

        for ((chunkedIndex, chunkedDaily) in dailies.chunked(CHUNKED_EXECUTED_COMMAND_LOGS_COUNT).withIndex()) {
            logger.info { "Doing bulk command stats... Current index: $chunkedIndex" }
            val start = System.currentTimeMillis()
            val commands = transaction(Connection.TRANSACTION_READ_UNCOMMITTED, 5, database) {
                ExecutedCommandsLog.slice(ExecutedCommandsLog.command, ExecutedCommandsLog.userId, commandCountField)
                        .select {
                            ExecutedCommandsLog.userId inList chunkedDaily.map { it[Dailies.receivedById] }
                        }
                        .groupBy(ExecutedCommandsLog.command, ExecutedCommandsLog.userId)
                        .orderBy(commandCountField, SortOrder.DESC)
                        .toList()
            }.groupBy { it[ExecutedCommandsLog.userId] }

            println("Finish: ${System.currentTimeMillis() - start}ms")

            for ((index, daily) in chunkedDaily.withIndex()) {
                if (index % 50 == 0)
                    logger.info { "${index + (CHUNKED_EXECUTED_COMMAND_LOGS_COUNT * chunkedIndex)}/${dailies.size}" }

                val userId = daily[Dailies.receivedById]
                val cmdQuantity = commands[userId]?.sumBy { it[commandCountField].toInt() } ?: continue
                val cmdEconomyQuantity = commands[userId]?.filter { it[ExecutedCommandsLog.command] in DailyCatcherManager.ECONOMY_COMMANDS }
                        ?.sumBy { it[commandCountField].toInt() } ?: continue
                val cmdEconomyLenientQuantity = commands[userId]?.filter { it[ExecutedCommandsLog.command] in DailyCatcherManager.LENIENT_ECONOMY_COMMANDS }
                        ?.sumBy { it[commandCountField].toInt() } ?: continue

                val percentage = cmdEconomyQuantity.toDouble() / cmdQuantity.toDouble()
                val percentageLenient = cmdEconomyLenientQuantity.toDouble() / cmdQuantity.toDouble()

                if (percentage >= ECONOMY_COMMANDS_THRESHOLD || percentageLenient >= LENIENT_ECONOMY_COMMANDS_THRESHOLD) {
                    println("PERCENTAGE WAS HIT!")

                    // A threshold has been reached! Time to check all the transactions and figure out who the account is related to
                    val stats = ExecutedCommandsStats(
                            cmdQuantity,
                            cmdEconomyQuantity,
                            cmdEconomyLenientQuantity
                    )

                    val sonhosTransactionsRelatedToTheUser = transaction(database) {
                        SonhosTransaction.select {
                            SonhosTransaction.givenBy eq userId and (SonhosTransaction.givenAt greaterEq DailyCatcherManager.yesterdayAtMidnight() and (SonhosTransaction.givenAt lessEq DailyCatcherManager.yesterdayBeforeDaySwitch()))
                        }.toList()
                    }

                    if (sonhosTransactionsRelatedToTheUser.isEmpty())
                        // If there isn't any transactions related, just skip for now
                        continue

                    val groupedBySortedReceivedBy = sonhosTransactionsRelatedToTheUser.groupBy {
                        it[SonhosTransaction.receivedBy] ?: -1L
                    }.entries.sortedByDescending { it.key }

                    val likelyToBeTheMainAccount = groupedBySortedReceivedBy.first()

                    val mainAccountGotDailyToday = transaction(database) {
                        Dailies.select {
                            Dailies.receivedById eq likelyToBeTheMainAccount.key and (Dailies.receivedAt greaterEq DailyCatcherManager.yesterdayAtMidnight() and (Dailies.receivedAt lessEq DailyCatcherManager.yesterdayBeforeDaySwitch()))
                        }.count()
                    }

                    // If the main account got daily today... then that's a big oof moment.
                    if (mainAccountGotDailyToday != 0L) {
                        val simpleCatcherReport = ReportOnlyEcoCatcher(
                                percentage,
                                stats,
                                listOf(
                                        userId,
                                        likelyToBeTheMainAccount.key
                                ),
                                likelyToBeTheMainAccount.value.map {
                                    convertToWrapper(it)
                                }
                        )

                        println(simpleCatcherReport)

                        channel.send(simpleCatcherReport)
                    }
                }
            }
        }

        channel.close()
    }

    override fun buildReportMessage(jda: JDA, bannedUsersIds: Set<Long>, report: ReportOnlyEcoCatcher): DailyCatcherMessage {
        var susLevel = SuspiciousLevel.SUS
        val userInfoCache = UserInfoCache()

        val embed = EmbedBuilder()

        report.users.forEach {
            val user = userInfoCache.getOrRetrieveUserInfo(jda, it)

            if (user?.avatarId == null)
                susLevel = susLevel.increase()
            if (user?.name?.contains("FAKE", true) == true)
                susLevel = susLevel.increase()
        }

        repeat(report.transactions.size / 5) {
            susLevel = susLevel.increase()
        }

        if (report.transactions.size == 1)
            susLevel = SuspiciousLevel.NOT_REALLY_SUS

        var reportMessage = appendHeader("Conta pegou daily e apenas usou comandos de economia na conta", susLevel)

        reportMessage += "Conta `${report.users[0]}` apenas usa comandos de economia (${report.commandsStats.formatted()}) e enviou para `${report.users[1]}`\n\n"

        if (susLevel == SuspiciousLevel.NOT_REALLY_SUS) {
            reportMessage += "Como a conta só transferiu apenas uma vez até agora, eu acho melhor esperar para ver o que acontece no futuro, para depois punir..."
            reportMessage += "\n\n"
        }

        val usersToBeBanned = report.users.filter { it !in bannedUsersIds }

        val userDailyRewardCache = UserDailyRewardCache()

        report.users.forEach {
            if (it in bannedUsersIds)
                reportMessage += "~~"

            val retrievedUser = userInfoCache.getOrRetrieveUserInfo(jda, it)

            val lastDailyReward = retrieveUserLastDailyReward(it)
            reportMessage += "**User:** `$it` (`${retrievedUser?.name}`) (${retrieveSonhos(it)} sonhos) (${retrieveExecutedCommandsStats(it).formatted()})\n"

            if (lastDailyReward != null) {
                reportMessage += "` `**Email:** `${lastDailyReward[Dailies.email]}`\n"
                reportMessage += "` `**IP:** `${lastDailyReward[Dailies.ip]}`\n"
                reportMessage += "` `**Daily pego:** `${DailyCatcherManager.formatDate(lastDailyReward[Dailies.receivedAt])}`\n"
            } else {
                // This should never happen... but sometimes it happens (oof)
                reportMessage += "` `**A pessoa nunca pegou daily...**\n"
            }

            if (it in bannedUsersIds)
                reportMessage += "~~"
        }

        if (usersToBeBanned.isNotEmpty()) {
            reportMessage += "\n"
            reportMessage += appendMeta(usersToBeBanned)
        }

        return DailyCatcherMessage(
                MessageBuilder(reportMessage)
                        .setEmbed(
                                embed
                                        .appendTransactionsToEmbed(report.transactions, userDailyRewardCache)
                                        .appendDailyList(report.users)
                                        .setTimestamp(Instant.now())
                                        .build()
                        )
                        .build(),
                susLevel,
                usersToBeBanned.isNotEmpty()
        )
    }
}