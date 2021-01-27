package net.perfectdreams.loritta.helper.utils.dailycatcher

import net.dv8tion.jda.api.entities.Message

data class DailyCatcherMessage(
        val message: Message,
        val suspiciousLevel: SuspiciousLevel,
        val addReactions: Boolean
)