package net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater

import org.jetbrains.exposed.dao.id.LongIdTable

object Dailies : LongIdTable() {
    val receivedById = long("received_by").index()
    val receivedAt = long("received_at").index()
    val ip = text("ip").index()
    val email = text("email").index()
    val userAgent = text("user_agent")
        .nullable()
    val browserFingerprints = optReference("browser_fingerprints", BrowserFingerprints)
}