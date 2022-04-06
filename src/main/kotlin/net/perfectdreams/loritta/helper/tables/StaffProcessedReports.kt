package net.perfectdreams.loritta.helper.tables

import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import net.perfectdreams.exposedpowerutils.sql.postgresEnumeration
import net.perfectdreams.loritta.helper.utils.StaffProcessedReportResult
import org.jetbrains.exposed.dao.id.LongIdTable

object StaffProcessedReports : LongIdTable() {
    val timestamp = timestampWithTimeZone("timestamp").index()
    val userId = long("user").index()
    val reporterId = long("reporter").index()
    val messageId = long("message")
    val result = postgresEnumeration<StaffProcessedReportResult>("result").index()
}