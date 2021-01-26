package net.perfectdreams.loritta.helper.utils.dailycatcher

data class ReportSameIpSuspicious(
    val users: List<UserAndEmail>,
    val transactions: List<SonhosTransactionWrapper>
)