package net.perfectdreams.loritta.helper.utils.dailycatcher

data class SonhosTransactionWrapper(
    val givenById: Long,
    val receivedById: Long,
    val quantity: Long,
    val givenAt: Long
)