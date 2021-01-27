package net.perfectdreams.loritta.helper.utils.dailycatcher

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.*
import net.perfectdreams.loritta.helper.utils.Constants
import org.apache.commons.text.similarity.LevenshteinDistance
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.Instant
import java.time.ZoneId

class DailyCatcher(val m: LorittaHelper, val jda: JDA) {
    companion object {
        val ECONOMY_COMMANDS = listOf(
            "PagarCommand",
            "DailyCommand",
            "LoraffleCommand",
            "SonhosCommand"
        )

        val SCARLET_POLICE_CHANNEL_ID = 803691195589984276L
        val SCARLET_POLICE_RESULTS_CHANNEL_ID = 803767073158463498L
        val ALREADY_NOTIFIED_IDS_FILE = File("already_notified_ids")

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
    }

    val alreadyNotifiedIds = mutableSetOf<Long>()
    val dailySimilarEmailsCatcher = DailySimilarEmailsCatcher(m.databases.lorittaDatabase)
    val dailySameIpCatcher = DailySameIpCatcher(m.databases.lorittaDatabase)
    var lastCheck = System.currentTimeMillis()

    private fun retrieveSonhos(userId: Long): Long? = transaction(m.databases.lorittaDatabase) {
        Profiles.select { Profiles.id eq userId }
            .firstOrNull()?.get(Profiles.money)
    }

    fun doReports() {
        val todayAtMidnight = todayAtMidnight()

        // Clear the already notified IDs if it is a new day
        if (todayAtMidnight >= lastCheck)
            alreadyNotifiedIds.clear()

        val bannedUsersIds = transaction(m.databases.lorittaDatabase) {
            BannedUsers.slice(BannedUsers.userId).selectAll().map { it[BannedUsers.userId] }
                .toSet()
        }

        val similarEmailsReports = dailySimilarEmailsCatcher.catch()

        val sentReportsSusLevel = mutableListOf<SuspiciousLevel>()
        sentReportsSusLevel.addAll(
            sendSimilarEmailsReports(
                bannedUsersIds,
                similarEmailsReports
            )
        )

        ALREADY_NOTIFIED_IDS_FILE.writeText(
            alreadyNotifiedIds.joinToString("\n")
        )

        val sameIpSuspiciousReports = dailySameIpCatcher.catch()

        sentReportsSusLevel.addAll(
            sendSameIpSuspiciousReports(
                bannedUsersIds,
                sameIpSuspiciousReports
            )
        )

        ALREADY_NOTIFIED_IDS_FILE.writeText(
            alreadyNotifiedIds.joinToString("\n")
        )

        if (sentReportsSusLevel.isNotEmpty()) {
            val sentReports = sentReportsSusLevel

            var message = "<:catpolice:585608392110899200> "
            val maxLevel = sentReports.maxOf { it.level }

            val notifyStaff = maxLevel >= SuspiciousLevel.VERY_SUS.level
            if (notifyStaff)
                message += "<@&351473717194522647> "

            val reportsByType = sentReports.groupBy { it }.entries
                .sortedByDescending { it.key.level }
                .joinToString(", ") { "${it.value.size} ${it.key.emote}" }

            message += "Hey, ${sentReports.size} ($reportsByType) denúncias da Polícia Escarlate chegaram! <#803691195589984276>"

            if (!notifyStaff)
                message += " (Decidi não notificar já que não tem reports muito sus para serem analisados)"

            val channel = jda.getTextChannelById(Constants.PORTUGUESE_STAFF_CHANNEL_ID)
            channel?.sendMessage(message)
                ?.complete()
        }

        val todayAtMidnight2 = todayAtMidnight()

        // Check again because the reports may take a while to finish, so it may start before midnight and finish after midnight!
        if (todayAtMidnight2 >= lastCheck)
            alreadyNotifiedIds.clear()

        lastCheck = System.currentTimeMillis()

        ALREADY_NOTIFIED_IDS_FILE.writeText(
            alreadyNotifiedIds.joinToString("\n")
        )
    }

    private fun appendHeader(type: String, suspiciousLevel: SuspiciousLevel): String {
        var userInput = ""

        userInput = "⸻⸻⸻⸻⸻⸻⸻⸻⸻\n"
        userInput += "<:catpolice:585608392110899200> **DENÚNCIA DA POLÍCIA ESCARLATE**\n"
        userInput += "<:lori_what:626942886361038868> **Tipo:** $type\n"
        userInput += "${suspiciousLevel.emote} **Nível de sus:** *${suspiciousLevel.text}*\n\n"

        return userInput
    }

    private fun appendUser(bannedUsersIds: Set<Long>, user: UserAndEmail, usersToBeMatchedAgainst: List<UserAndEmail>): String {
        var userInput = ""

        if (user.userId in bannedUsersIds)
            userInput += "~~"

        userInput += "**User:** `${user.userId}` (${retrieveSonhos(user.userId)} sonhos)\n"
        userInput += "` `**Email:** `${user.email}`\n"
        userInput += "` `**IP:** `${user.ip}`\n"
        userInput += "` `**Daily pego:** `${formatDate(user.lastDailyAt)}`\n"

        for (innerUser in usersToBeMatchedAgainst) {
            if (innerUser == user)
                continue

            if (user.email.split("@")[0] == innerUser.email.split("@")[0]) {
                userInput += "`   `**É literalmente o mesmo endereço!** `${user.email}` e `${innerUser.email}`\n"
            }
        }

        if (user.userId in bannedUsersIds)
            userInput += "~~"

        return userInput
    }

    fun sendSameIpSuspiciousReports(bannedUsersIds: Set<Long>, sameIpReports: MutableList<ReportSameIpSuspicious>): MutableList<SuspiciousLevel> {
        val sentReportsSusLevel = mutableListOf<SuspiciousLevel>()

        val levenshtein = LevenshteinDistance(3)

        // Only accounts with transactions are notified to avoid taking sooooo long to send all the messages
        for (it in sameIpReports.filter { it.transactions.isNotEmpty() }) {
            val notNotifiedYetUsers = it.users.filter { it.userId !in alreadyNotifiedIds }
            if (notNotifiedYetUsers.isEmpty())
                continue // All users from this were already notified

            val usersToBeBanned = it.users.filter { it.userId !in bannedUsersIds }

            var susLevel = if (it.transactions.isEmpty())
                SuspiciousLevel.NOT_REALLY_SUS
            else {
                SuspiciousLevel.SUS
            }

            for (user in it.users) {
                for (innerUser in it.users.filter { it != user }) {
                    val threshold = levenshtein.apply(
                        user.email.split("@")[0],
                        innerUser.email.split("@")[0]
                    )

                    if (threshold == 0) {
                        susLevel = SuspiciousLevel.TOTALLY_THE_SAME_USER
                        break
                    }

                    if (threshold != -1) {
                        susLevel = SuspiciousLevel.VERY_SUS
                        break
                    }
                }
            }

            var report = appendHeader("Daily no mesmo IP", susLevel)

            for (user in it.users) {
                report += appendUser(bannedUsersIds, user, it.users)
                report += "\n"
            }

            report += "\n"
            if (it.transactions.isNotEmpty()) {
                report += "**Transações relacionadas:**\n"

                for (transaction in it.transactions) {
                    val givenByEmail = it.users.firstOrNull { it.userId == transaction.givenById }?.email ?: "???"
                    val receivedByEmail = it.users.firstOrNull { it.userId == transaction.receivedById }?.email ?: "???"

                    val addToInput = "`[${formatDate(transaction.givenAt)}]` `${transaction.givenById}` (`$givenByEmail`) -> `${transaction.receivedById}` (`$receivedByEmail`) - ${transaction.quantity} sonhos\n"

                    if (report.length + addToInput.length > 1500)
                        break

                    report += addToInput
                }
            } else {
                report += "**Pelo visto nenhuma das contas transferiu sonhos entre si... Mas lembre de verificar se a conta transferiu para outras pessoas!**\n"
            }

            if (usersToBeBanned.isNotEmpty())
                report += "\n**Meta:** ||${usersToBeBanned.joinToString(";") { it.userId.toString() }}||"

            alreadyNotifiedIds.addAll(it.users.map { it.userId })

            val channel = jda.getTextChannelById(SCARLET_POLICE_CHANNEL_ID)

            val message = channel?.sendMessage(report)?.complete()

            if (usersToBeBanned.isNotEmpty()) {
                message?.addReaction("sasuke_banido:750509326782824458")
                    ?.complete()
                message?.addReaction("error:412585701054611458")
                    ?.complete()
            }

            sentReportsSusLevel.add(susLevel)
        }

        return sentReportsSusLevel
    }

    fun sendSimilarEmailsReports(bannedUsersIds: Set<Long>, similarEmailsReport: List<ReportSimilarEmails>): MutableList<SuspiciousLevel> {
        val sentReportsSusLevel = mutableListOf<SuspiciousLevel>()

        for (it in similarEmailsReport) {
            val notNotifiedYetUsers = it.users.filter { it.userId !in alreadyNotifiedIds }
            if (notNotifiedYetUsers.isEmpty())
                continue // All users from this were already notified

            val usersToBeBanned = it.users.filter { it.userId !in bannedUsersIds }

            var susLevel = SuspiciousLevel.TOTALLY_THE_SAME_USER
            outerLoop@for (user in it.users) {
                for (x in it.users) {
                    if (user.email.split("@")[0] != x.email.split("@")[0]) {
                        susLevel = SuspiciousLevel.VERY_SUS
                        break@outerLoop
                    }
                }
            }

            var report = appendHeader("Endereços de email similares", susLevel)

            for (user in it.users) {
                report += appendUser(bannedUsersIds, user, it.users)
                report += "\n"
            }

            report += "\n"
            if (it.transactions.isNotEmpty()) {
                report += "**Transações relacionadas:**\n"

                for (transaction in it.transactions) {
                    val givenByEmail = it.users.firstOrNull { it.userId == transaction.givenById }?.email ?: "???"
                    val receivedByEmail = it.users.firstOrNull { it.userId == transaction.receivedById }?.email ?: "???"

                    val addToInput = "`[${formatDate(transaction.givenAt)}]` `${transaction.givenById}` (`$givenByEmail`) -> `${transaction.receivedById}` (`$receivedByEmail`) - ${transaction.quantity} sonhos\n"

                    if (report.length + addToInput.length > 1500)
                        break

                    report += addToInput
                }
            } else {
                report += "**Pelo visto nenhuma das contas transferiu sonhos entre si... Mas lembre de verificar se a conta transferiu para outras pessoas!**\n"
            }

            if (usersToBeBanned.isNotEmpty())
                report += "\n**Meta:** ||${usersToBeBanned.joinToString(";") { it.userId.toString() }}||"

            alreadyNotifiedIds.addAll(it.users.map { it.userId })

            val channel = jda.getTextChannelById(SCARLET_POLICE_CHANNEL_ID)

            val message = channel?.sendMessage(report)?.complete()

            if (usersToBeBanned.isNotEmpty()) {
                message?.addReaction("sasuke_banido:750509326782824458")
                    ?.complete()
                message?.addReaction("error:412585701054611458")
                    ?.complete()
            }

            sentReportsSusLevel.add(susLevel)
        }

        return sentReportsSusLevel
    }

    fun formatDate(time: Long): String {
        val givenAtTime = Instant.ofEpochMilli(time)
            .atZone(ZoneId.systemDefault())

        val day = givenAtTime.dayOfMonth.toString().padStart(2, '0')
        val month = givenAtTime.monthValue.toString().padStart(2, '0')
        val year = givenAtTime.year

        val hour = givenAtTime.hour.toString().padStart(2, '0')
        val minute = givenAtTime.minute.toString().padStart(2, '0')
        val second = givenAtTime.second.toString().padStart(2, '0')

        return "$day/$month/$year $hour:$minute:$second"
    }
}