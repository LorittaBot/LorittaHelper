package net.perfectdreams.loritta.helper.tables

import net.perfectdreams.loritta.cinnamon.pudding.utils.exposed.postgresEnumeration
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SelectedResponsesLog : LongIdTable() {
    val timestamp = timestamp("timestamp")
    val ticketSystemType = postgresEnumeration<TicketUtils.TicketSystemType>("ticket_system_type")
    val userId = long("user").index()
    val selectedResponse = text("selected_response").index()
}