package net.perfectdreams.loritta.helper.utils.dailycatcher

data class UserAndEmail(
    val userId: Long,
    val ip: String,
    val email: String,
    val lastDailyAt: Long
)