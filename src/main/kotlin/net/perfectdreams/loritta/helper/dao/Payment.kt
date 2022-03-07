package net.perfectdreams.loritta.helper.dao

import net.perfectdreams.loritta.cinnamon.pudding.tables.Payments
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Payment(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Payment>(Payments)

    var userId by Payments.userId
    var gateway by Payments.gateway
    var reason by Payments.reason
    var money by Payments.money
    var createdAt by Payments.createdAt
    var paidAt by Payments.paidAt
    var expiresAt by Payments.expiresAt
    var discount by Payments.discount
    // var metadata by Payments.metadata
    var referenceId by Payments.referenceId
}