package net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater

import org.jetbrains.exposed.dao.id.LongIdTable

object BrowserFingerprints : LongIdTable() {
    val width = integer("width")
    val height = integer("height")
    val availWidth = integer("avail_width")
    val availHeight = integer("avail_height")
    val accept = text("accept").nullable()
    val contentLanguage = text("content_language").nullable()
    val timezoneOffset = integer("timezone_offset")
    val clientId = uuid("client_id").index()
}