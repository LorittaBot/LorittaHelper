package net.perfectdreams.loritta.helper.tables

import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import net.perfectdreams.exposedpowerutils.sql.postgresEnumeration
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import org.jetbrains.exposed.dao.id.LongIdTable

object SelectedResponsesLog : LongIdTable() {
    val timestamp = timestampWithTimeZone("timestamp").index()
    val ticketSystemType = postgresEnumeration<TicketUtils.TicketSystemType>("ticket_system_type")
    val userId = long("user").index()
    val selectedResponse = text("selected_response").index()
}