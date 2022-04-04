package net.perfectdreams.loritta.helper.tables

import net.perfectdreams.exposedpowerutils.sql.javatime.timestampWithTimeZone
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketMessagesActivity : LongIdTable() {
    val userId = long("user").index()
    val messageId = long("message")
    val timestamp = timestampWithTimeZone("timestamp")
    val supportSolicitationId = reference("support_solicitation", StartedSupportSolicitations).index()
}