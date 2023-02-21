package net.perfectdreams.loritta.helper.utils.dailycatcher

import net.dv8tion.jda.api.utils.messages.MessageCreateData

data class DailyCatcherMessage(
        val message: MessageCreateData,
        val suspiciousLevel: SuspiciousLevel,
        val addReactions: Boolean
)