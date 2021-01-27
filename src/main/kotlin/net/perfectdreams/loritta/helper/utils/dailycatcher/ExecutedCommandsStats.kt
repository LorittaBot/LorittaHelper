package net.perfectdreams.loritta.helper.utils.dailycatcher

data class ExecutedCommandsStats(
        val total: Int,
        val economyCommands: Int,
        val lenientEconomyCommands: Int
) {
    fun formatted() = "$economyCommands/$lenientEconomyCommands/$total"
}