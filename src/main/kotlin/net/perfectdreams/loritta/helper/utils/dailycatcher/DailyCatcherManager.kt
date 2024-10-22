package net.perfectdreams.loritta.helper.utils.dailycatcher

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.dailycatcher.catchers.DailyOnlyEcoCommandsCatcher
import net.perfectdreams.loritta.helper.utils.dailycatcher.reports.ReportOnlyEcoCatcher
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneId

class DailyCatcherManager(val m: LorittaHelper, val jda: JDA) {
    companion object {
        val ECONOMY_COMMANDS = setOf(
                "PagarCommand",
                "DailyCommand",
                "DiscordBotListCommand",
                "SonhosCommand"
        )

        val LENIENT_ECONOMY_COMMANDS = ECONOMY_COMMANDS + setOf(
                "LoraffleCommand",
                "CoinFlipBetCommand",
                "EmojiFightBetCommand",
                "RepCommand",
                "TransactionsCommand"
        )

        val SCARLET_POLICE_CHANNEL_ID = 803691195589984276L
        val SCARLET_POLICE_RESULTS_CHANNEL_ID = 803767073158463498L
        private val logger = KotlinLogging.logger {}

        fun yesterdayAtMidnight() = Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toOffsetDateTime()
                .minusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .toInstant()
                .toEpochMilli()

        fun sevenDaysAgoAtMidnight() = Instant.now()
            .atZone(ZoneId.of("America/Sao_Paulo"))
            .toOffsetDateTime()
            .minusDays(7)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .toInstant()
            .toEpochMilli()

        fun yesterdayBeforeDaySwitch() = Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toOffsetDateTime()
                .minusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .toInstant()
                .toEpochMilli()

        fun todayAtMidnight() = Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toOffsetDateTime()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .toInstant()
                .toEpochMilli()

        fun fourteenDaysAgo() = Instant.now()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toOffsetDateTime()
                .minusDays(14)
                .toInstant()
                .toEpochMilli()

        fun formatDate(time: Long): String {
            val givenAtTime = Instant.ofEpochMilli(time)
                    .atZone(ZoneId.of("America/Sao_Paulo"))

            val day = givenAtTime.dayOfMonth.toString().padStart(2, '0')
            val month = givenAtTime.monthValue.toString().padStart(2, '0')
            val year = givenAtTime.year

            val hour = givenAtTime.hour.toString().padStart(2, '0')
            val minute = givenAtTime.minute.toString().padStart(2, '0')
            val second = givenAtTime.second.toString().padStart(2, '0')

            return "$day/$month/$year $hour:$minute:$second"
        }
    }

    private val community = m.config.guilds.community

    // ===[ CATCHERS ]===
    val dailyOnlyEcoCommandsCatcher = DailyOnlyEcoCommandsCatcher(m.databases.lorittaDatabase)

    fun doReports() {
        val bannedUsersIds = transaction(m.databases.lorittaDatabase) {
            BannedUsers.select(BannedUsers.userId)
                .where {
                    (BannedUsers.valid eq true) and
                            (BannedUsers.expiresAt.isNull() or (BannedUsers.expiresAt.isNotNull() and BannedUsers.expiresAt.greaterEq(
                                System.currentTimeMillis()
                            )))
                }
                .map { it[BannedUsers.userId] }
                .toSet()
        }

        val portugueseStaffChannel = jda.getTextChannelById(community.channels.staff)
        portugueseStaffChannel?.sendMessage("Buscando contas fakes... <a:among_us_vent:759519990150856794>")
                ?.complete()

        val channel = Channel<ReportOnlyEcoCatcher>()

        logger.info { "Verifying users that never used any commands except economy commands" }

        val onlyEcoCommandsCatcher = mutableListOf<Job>()

        val sentReportsSusLevel = mutableListOf<SuspiciousLevel>()

        onlyEcoCommandsCatcher += m.launch {
            dailyOnlyEcoCommandsCatcher.catch(channel)
        }

        onlyEcoCommandsCatcher += m.launch {
            for (report in channel) {
                val message = dailyOnlyEcoCommandsCatcher.buildReportMessage(
                        jda,
                        bannedUsersIds,
                        report
                )

                sendReportMessage(message)
                sentReportsSusLevel.add(message.suspiciousLevel)
            }
        }

        runBlocking {
            onlyEcoCommandsCatcher.joinAll()
        }

        if (sentReportsSusLevel.isNotEmpty()) {
            val sentReports = sentReportsSusLevel

            var message = "<:catpolice:585608392110899200> "
            val maxLevel = sentReports.maxOf { it.level }

            val notifyStaff = maxLevel >= SuspiciousLevel.VERY_SUS.level
            if (notifyStaff)
                message += "<@&${community.roles.loriBodyguards}> "

            val reportsByType = sentReports.groupBy { it }.entries
                    .sortedByDescending { it.key.level }
                    .joinToString(", ") { "${it.value.size} ${it.key.emote}" }

            message += "Hey, ${sentReports.size} ($reportsByType) denúncias da Polícia Escarlate chegaram! <#803691195589984276>"

            if (!notifyStaff)
                message += " (Decidi não notificar já que não tem reports muito sus para serem analisados)"

            portugueseStaffChannel?.sendMessage(message)
                    ?.complete()
        }
    }

    private fun sendReportMessage(reportMessage: DailyCatcherMessage) {
        val channel = jda.getTextChannelById(SCARLET_POLICE_CHANNEL_ID)

        val message = channel?.sendMessage(reportMessage.message)?.complete()

        if (reportMessage.addReactions) {
            message?.addReaction(Emoji.fromCustom("sasuke_banido", 750509326782824458, true))
                    ?.complete()
            message?.addReaction(Emoji.fromCustom("error", 412585701054611458, false))
                    ?.complete()
        }
    }
}