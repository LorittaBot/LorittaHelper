package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.builder.message.embed
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.utils.footer
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.StaffProcessedReports
import net.perfectdreams.loritta.helper.tables.TicketMessagesActivity
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class StatsTicketsExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.HELPER) {
    companion object : SlashCommandExecutorDeclaration(StatsTicketsExecutor::class) {
        override val options = Options

        object Options : ApplicationCommandOptions() {
            val filter = optionalString("filter", "Filtro de data")
                .choice("7", "Últimos 7 dias")
                .choice("14", "Últimos 14 dias")
                .choice("30", "Últimos 30 dias")
                .choice("90", "Últimos 90 dias")
                .choice("365", "Últimos 365 dias")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val filterDay = args[options.filter]

        // When using Instant.MIN, this happened:
        // "DefaultDispatcher-worker-2" java.time.DateTimeException: Invalid value for EpochDay (valid values -365243219162 - 365241780471): -365243219528
        var since = Instant.ofEpochMilli(0)

        if (filterDay != null)
            since = Instant.now().minusSeconds(filterDay.toLong() * 86400)

        val result = transaction(helper.databases.helperDatabase) {
            val resultCount = TicketMessagesActivity.supportSolicitationId.countDistinct()

            val currentBanStatus = TicketMessagesActivity.slice(TicketMessagesActivity.userId, resultCount).select {
                StaffProcessedReports.timestamp greaterEq since
            }
                .groupBy(TicketMessagesActivity.userId)
                .toList()

            currentBanStatus.map {
                UserStatsResult(
                    it[TicketMessagesActivity.userId],
                    it[resultCount]
                )
            }
        }


        context.sendMessage {
            embed {
                title = "Ranking de Tickets Respondidos"

                description = buildString {
                    for ((index, userStats) in result.sortedByDescending { it.ticketsReplied }.withIndex()) {
                        append("**${index + 1}.** <@${userStats.userId}> - ${userStats.ticketsReplied} tickets respondidos")
                        append("\n")
                    }
                }

                footer("Burocracia my beloved")
            }
        }
    }

    private class UserStatsResult(
        val userId: Long,
        val ticketsReplied: Long
    )
}
