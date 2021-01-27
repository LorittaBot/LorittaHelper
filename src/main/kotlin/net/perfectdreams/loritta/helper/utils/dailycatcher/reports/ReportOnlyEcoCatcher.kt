package net.perfectdreams.loritta.helper.utils.dailycatcher.reports

import net.perfectdreams.loritta.helper.utils.dailycatcher.ExecutedCommandsStats
import net.perfectdreams.loritta.helper.utils.dailycatcher.SonhosTransactionWrapper

data class ReportOnlyEcoCatcher(
        val threshold: Double,
        val commandsStats: ExecutedCommandsStats,
        val users: List<Long>,
        val transactions: List<SonhosTransactionWrapper>
)