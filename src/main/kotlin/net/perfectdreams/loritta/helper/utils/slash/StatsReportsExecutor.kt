package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.builder.message.embed
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.utils.footer
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.listeners.ApproveReportsOnReactionListener
import net.perfectdreams.loritta.helper.tables.StaffProcessedReports
import net.perfectdreams.loritta.helper.utils.StaffProcessedReportResult
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class StatsReportsExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val filter = optionalString("filter", "Filtro de data") {
            choice("Últimos 7 dias", "7")
            choice("Últimos 14 dias", "14")
            choice("Últimos 30 dias", "30")
            choice("Últimos 90 dias", "90")
            choice("Últimos 365 dias", "365")
        }
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val filterDay = args[options.filter]

        // When using Instant.MIN, this happened:
        // "DefaultDispatcher-worker-2" java.time.DateTimeException: Invalid value for EpochDay (valid values -365243219162 - 365241780471): -365243219528
        var since = Instant.ofEpochMilli(0)

        if (filterDay != null)
            since = Instant.now().minusSeconds(filterDay.toLong() * 86400)

        val result = transaction(helper.databases.helperDatabase) {
            val resultCount = StaffProcessedReports.result.count()

            val currentBanStatus = StaffProcessedReports.slice(StaffProcessedReports.userId, StaffProcessedReports.result, resultCount).select {
                StaffProcessedReports.timestamp greaterEq since
            }
                .groupBy(StaffProcessedReports.userId, StaffProcessedReports.result)
                .toList()

            currentBanStatus.map { it[StaffProcessedReports.userId] }.toSet().map { userId ->
                UserStatsResult(
                    userId,
                    currentBanStatus.firstOrNull { it[StaffProcessedReports.userId] == userId && it[StaffProcessedReports.result] == StaffProcessedReportResult.APPROVED }?.getOrNull(resultCount) ?: 0,
                    currentBanStatus.firstOrNull { it[StaffProcessedReports.userId] == userId && it[StaffProcessedReports.result] == StaffProcessedReportResult.REJECTED }?.getOrNull(resultCount) ?: 0
                )
            }
        }


        context.sendMessage {
            embed {
                title = "Ranking de Denúncias Processadas"

                description = buildString {
                    for ((index, userStats) in result.sortedByDescending { it.approved + it.rejected }.withIndex()) {
                        append("**${index + 1}.** <@${userStats.userId}> - ${userStats.approved + userStats.rejected} (${userStats.approved} ${ApproveReportsOnReactionListener.APPROVE_EMOTE} | ${userStats.rejected} ${ApproveReportsOnReactionListener.REJECT_EMOTE}) denúncias processadas")
                        append("\n")
                    }
                }

                footer("Burocracia my beloved")
            }
        }
    }

    private class UserStatsResult(
        val userId: Long,
        val approved: Long,
        val rejected: Long
    )
}
