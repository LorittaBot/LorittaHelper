package net.perfectdreams.loritta.helper.serverresponses

import net.perfectdreams.loritta.api.messages.LorittaReply

data class AutomatedSupportResponse(
    val replies: List<LorittaReply>,
    val includeCloseTicketCallToAction: Boolean,
)