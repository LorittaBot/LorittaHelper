package net.perfectdreams.loritta.helper.utils.dailycatcher

data class ReportSimilarEmails(
    val users: List<UserAndEmail>,
    val transactions: List<SonhosTransactionWrapper>
)