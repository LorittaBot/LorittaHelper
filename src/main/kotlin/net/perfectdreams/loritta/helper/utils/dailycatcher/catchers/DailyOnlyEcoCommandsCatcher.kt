package net.perfectdreams.loritta.helper.utils.dailycatcher.catchers

import kotlinx.coroutines.channels.Channel
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.perfectdreams.loritta.cinnamon.pudding.tables.Dailies
import net.perfectdreams.loritta.helper.tables.ExecutedCommandsLog
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherMessage
import net.perfectdreams.loritta.helper.utils.dailycatcher.ExecutedCommandsStats
import net.perfectdreams.loritta.helper.utils.dailycatcher.SuspiciousLevel
import net.perfectdreams.loritta.helper.utils.dailycatcher.UserDailyRewardCache
import net.perfectdreams.loritta.helper.utils.dailycatcher.UserInfoCache
import net.perfectdreams.loritta.helper.utils.dailycatcher.reports.ReportOnlyEcoCatcher
import org.apache.commons.text.similarity.LevenshteinDistance
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.time.Instant

class DailyOnlyEcoCommandsCatcher(database: Database) : DailyCatcher<ReportOnlyEcoCatcher>(database) {
    companion object {
        private const val ECONOMY_COMMANDS_THRESHOLD = 0.95
        private const val LENIENT_ECONOMY_COMMANDS_THRESHOLD = 0.98
        private const val CHUNKED_EXECUTED_COMMAND_LOGS_COUNT = 500
        private val logger = KotlinLogging.logger {}

        private val SUSPICIOUS_NAMES = setOf(
            "FAKE",
            "sonhos",
            "farm"
        )
    }

    override suspend fun catch(channel: Channel<ReportOnlyEcoCatcher>) {
        val dailies = transaction(Connection.TRANSACTION_READ_UNCOMMITTED, db = database) {
            Dailies.selectAll()
                .where { Dailies.receivedAt greaterEq DailyCatcherManager.yesterdayAtMidnight() and (Dailies.receivedAt lessEq DailyCatcherManager.yesterdayBeforeDaySwitch()) }
                .toList()
        }

        // bulk stats
        val commandCountField = ExecutedCommandsLog.command.count()

        for ((chunkedIndex, chunkedDaily) in dailies.chunked(CHUNKED_EXECUTED_COMMAND_LOGS_COUNT).withIndex()) {
            logger.info { "Doing bulk command stats... Current index: $chunkedIndex" }
            val start = System.currentTimeMillis()
            val commands = transaction(Connection.TRANSACTION_READ_UNCOMMITTED, db = database) {
                ExecutedCommandsLog.select(ExecutedCommandsLog.command, ExecutedCommandsLog.userId, commandCountField)
                    .where {
                        ExecutedCommandsLog.sentAt greaterEq DailyCatcherManager.fourteenDaysAgo() and (
                                ExecutedCommandsLog.userId inList chunkedDaily.map { it[Dailies.receivedById] }
                                )
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
                        SonhosTransaction.selectAll()
                            .where { SonhosTransaction.givenBy eq userId and (SonhosTransaction.givenAt greaterEq DailyCatcherManager.sevenDaysAgoAtMidnight() and (SonhosTransaction.givenAt lessEq DailyCatcherManager.yesterdayBeforeDaySwitch())) }
                            .toList()
                    }

                    if (sonhosTransactionsRelatedToTheUser.isEmpty())
                    // If there isn't any transactions related, just skip for now
                        continue

                    val groupedBySortedReceivedBy = sonhosTransactionsRelatedToTheUser.groupBy {
                        it[SonhosTransaction.receivedBy] ?: -1L
                    }.entries.sortedByDescending { it.key }

                    val likelyToBeTheMainAccount = groupedBySortedReceivedBy.first()

                    val mainAccountGotDailyToday = transaction(database) {
                        Dailies.selectAll()
                            .where { Dailies.receivedById eq likelyToBeTheMainAccount.key and (Dailies.receivedAt greaterEq DailyCatcherManager.yesterdayAtMidnight() and (Dailies.receivedAt lessEq DailyCatcherManager.yesterdayBeforeDaySwitch())) }
                            .count()
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
        var susLevel = SuspiciousLevel.NOT_REALLY_SUS
        val userInfoCache = UserInfoCache()

        val embed = EmbedBuilder()

        /**
         * Only replace the suspicious level if the new suspicious level is higher than the previous level
         */
        fun replaceSusLevelIfHigher(newSuspiciousLevel: SuspiciousLevel) {
            if (newSuspiciousLevel.level > susLevel.level)
                susLevel = newSuspiciousLevel
        }

        report.users.forEach {
            val user = userInfoCache.getOrRetrieveUserInfo(jda, it)

            if (user?.avatarId == null)
                susLevel = susLevel.increase()

            // If the user has suspicious names in its name... (and similar) then well, the sus level goes off the roof!
            if (SUSPICIOUS_NAMES.any { user?.name?.contains("FAKE", true) == true })
                replaceSusLevelIfHigher(SuspiciousLevel.MEGA_VERY_SUS)

            if (user != null) {
                report.users.filter { other -> other != it }.forEach {
                    val otherUser = userInfoCache.getOrRetrieveUserInfo(jda, it) ?: return@forEach

                    if (otherUser.name.contains(user.name, true)) {
                        // If anyone else in the report has the same name, then oof, they ARE the same user because the chances of them not being the same user are very slim
                        replaceSusLevelIfHigher(SuspiciousLevel.TOTALLY_THE_SAME_USER)
                    }
                }
            }
        }

        // Avoid querying for similar emails if the sus level is already "totally the same user"!
        if (susLevel != SuspiciousLevel.TOTALLY_THE_SAME_USER) {
            val emails = report.users.mapNotNull { retrieveUserLastDailyReward(it) }.map { it[Dailies.email] }

            for (email in emails) {
                for (otherEmail in emails) {
                    // Ignore if we are checking the same email (which *should* be impossible)
                    if (email == otherEmail)
                        continue

                    // Check for email similarity, but ignore the host
                    // We also remove "+" and "." because they are common ways to allow emails to be redirected to your main inbox
                    val firstHalfEmail = email.split("@")
                        .first()
                        .replace("+", "")
                        .replace(".", "")
                    val firstHalfOtherEmail = otherEmail.split("@")
                        .first()
                        .replace("+", "")
                        .replace(".", "")

                    val levenshtein = LevenshteinDistance(2)
                    val result = levenshtein.apply(firstHalfEmail, firstHalfOtherEmail)

                    // Same user bye
                    if (result == 0)
                        replaceSusLevelIfHigher(SuspiciousLevel.TOTALLY_THE_SAME_USER)
                    else if (result > 0) // If the distance is positive, then it means that there was a match!
                        replaceSusLevelIfHigher(SuspiciousLevel.MEGA_VERY_SUS)

                    // If the result is negative then it means that the email is different, so not *sus*
                }
            }
        }

        // If the sus level is mega very sus, we are going to check if the IPs are equal and, if they are, we are going to upgrade the check to totally the same user!
        if (susLevel == SuspiciousLevel.MEGA_VERY_SUS) {
            val ips = report.users.mapNotNull { retrieveUserLastDailyReward(it) }.map { it[Dailies.ip] }

            // If we distinct the IPs and there's only one IP left, then it means that they have the same IP!
            if (ips.distinct().size == 1)
                susLevel = SuspiciousLevel.TOTALLY_THE_SAME_USER
        }

        repeat(report.transactions.size / 10) {
            if (SuspiciousLevel.SUPER_VERY_SUS.level > susLevel.level)
                susLevel = susLevel.increase()
        }

        if (report.transactions.size == 1)
            replaceSusLevelIfHigher(SuspiciousLevel.NOT_REALLY_SUS)

        var reportMessage = appendHeader("Conta pegou daily e apenas usou comandos de economia na conta", susLevel)

        reportMessage += "Conta `${report.users[0]}` apenas usou comandos de economia (${report.commandsStats.formatted()}) nos últimos 14 dias e enviou para `${report.users[1]}`\n\n"

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
                reportMessage += "` `**User Agent:** `${lastDailyReward[Dailies.userAgent]}`\n"
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
            MessageCreateBuilder()
                .setContent(reportMessage)
                .setEmbeds(
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