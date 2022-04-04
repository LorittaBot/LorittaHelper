package net.perfectdreams.loritta.helper.tables

import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import net.perfectdreams.exposedpowerutils.sql.postgresEnumeration
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import org.jetbrains.exposed.dao.id.LongIdTable

object StartedSupportSolicitations : LongIdTable() {
    val userId = long("user").index()
    val threadId = long("thread").index()
    val startedAt = timestampWithTimeZone("started_at").index()
    val systemType = postgresEnumeration<TicketUtils.TicketSystemType>("system_type").index()
}