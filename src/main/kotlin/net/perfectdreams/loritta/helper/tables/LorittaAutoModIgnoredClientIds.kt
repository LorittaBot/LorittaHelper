package net.perfectdreams.loritta.helper.tables

import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import net.perfectdreams.exposedpowerutils.sql.postgresEnumeration
import net.perfectdreams.loritta.helper.utils.StaffProcessedReportResult
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable

object LorittaAutoModIgnoredClientIds : LongIdTable() {
    val clientId = uuid("client_id").index()
    val addedAt = timestampWithTimeZone("added_at")
    val addedBy = long("added_by")
    val reason = text("reason")
}