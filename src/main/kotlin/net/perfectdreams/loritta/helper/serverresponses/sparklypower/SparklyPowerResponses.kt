package net.perfectdreams.loritta.helper.serverresponses.sparklypower

/**
 * Class holding a list containing all Loritta Helper's automatic responses (Portuguese)
 */
object SparklyPowerResponses {
    val responses = listOf(
        HowToBuyPesadelosResponse(),
        HowToEarnPesadelosResponse(),
        HowToEarnSonecasResponse(),
        HowToRegisterResponse(),
        HowToResetPasswordResponse(),
        HowToTransferSonhosResponse(),
        HowToVoteResponse()
    ).sortedByDescending { it.priority }
}
