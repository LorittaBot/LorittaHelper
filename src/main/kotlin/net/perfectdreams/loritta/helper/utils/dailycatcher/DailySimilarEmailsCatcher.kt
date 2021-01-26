package net.perfectdreams.loritta.helper.utils.dailycatcher

import net.perfectdreams.loritta.helper.tables.Dailies
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.SonhosPaymentReason
import org.apache.commons.text.similarity.LevenshteinDistance
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DailySimilarEmailsCatcher(val database: Database) {
    companion object {
        private val SIMILAR_EMAILS_THRESHOLD = 1
    }
    fun catch(): List<ReportSimilarEmails> {
        val dailyEmails = transaction(database) {
            Dailies.slice(Dailies.email).select {
                Dailies.receivedAt greaterEq DailyCatcher.todayAtMidnight()
            }.groupBy(Dailies.email)
                .map { it[Dailies.email].split("@") }
        }

        println(dailyEmails.size)

        var dupesFound = 0

        // Check very similar emails
        val levensthein = LevenshteinDistance(SIMILAR_EMAILS_THRESHOLD)

        val controls = mutableListOf<MutableSet<String>>()
        for ((index, email) in dailyEmails.withIndex()) {
            if (index % 100 == 0)
                println("$index/${dailyEmails.size} - Dupes: $dupesFound")

            for (checkedAgainstEmail in dailyEmails) {
                // Do not check the same email
                if (email != checkedAgainstEmail) {
                    val checkResult = levensthein.apply(email[0], checkedAgainstEmail[0])

                    val emailAddress = "${email[0]}@${email[1]}"
                    val checkedAgainstEmailAddress = "${checkedAgainstEmail[0]}@${checkedAgainstEmail[1]}"

                    if (checkResult != -1) {
                        println("$email ~ $checkedAgainstEmail = $checkResult")

                        val list = controls.firstOrNull { emailAddress in it } ?: run {
                            mutableSetOf<String>().also {
                                controls.add(it)
                            }
                        }

                        list.add(emailAddress)
                        list.add(checkedAgainstEmailAddress)

                        dupesFound++
                    }
                }
            }
        }

        val reports = mutableListOf<ReportSimilarEmails>()

        val matchedEmailsDailies = transaction(database) {
            Dailies.select {
                Dailies.email inList controls.flatten()
            }.orderBy(Dailies.receivedAt, SortOrder.DESC)
                .toList()
        }

        val fourteenDaysAgo = DailyCatcher.fourteenDaysAgo()

        controls.forEach {
            val userAndEmail = mutableListOf<UserAndEmail>()

            for (email in it) {
                val firstMatch = matchedEmailsDailies.firstOrNull { it[Dailies.email] == email }
                userAndEmail.add(
                    UserAndEmail(
                        firstMatch?.get(Dailies.receivedById) ?: -1L,
                        firstMatch?.get(Dailies.ip) ?: "???",
                        email,
                        firstMatch?.get(Dailies.receivedAt) ?: 0L
                    )
                )
            }

            val alsoGivenTransactions = transaction(database) {
                SonhosTransaction.select {
                    (SonhosTransaction.receivedBy inList userAndEmail.map { it.userId }).and(SonhosTransaction.givenBy inList userAndEmail.map { it.userId })
                        .and(SonhosTransaction.reason eq SonhosPaymentReason.PAYMENT)
                        .and(SonhosTransaction.givenAt greaterEq fourteenDaysAgo)
                }.orderBy(SonhosTransaction.givenAt, SortOrder.DESC)
                    .toList()
            }

            reports.add(
                ReportSimilarEmails(
                    userAndEmail,
                    alsoGivenTransactions.map {
                        SonhosTransactionWrapper(
                            it[SonhosTransaction.givenBy] ?: -1,
                            it[SonhosTransaction.receivedBy] ?: -1,
                            it[SonhosTransaction.quantity].toLong(),
                            it[SonhosTransaction.givenAt]
                        )
                    }
                )
            )
        }

        return reports
    }
}