package com.example.demo.entity.order.repository

import com.example.demo.entity.order.models.DeliveryDetails
import com.example.demo.entity.order.models.GdDelivery
import com.example.demo.entity.order.models.Order
import com.yourpackage.generated.Tables.LOGISTICS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class LogisticsRepository(
    private val dslContext: DSLContext,
) {
    @Transactional
    fun createLogistics(deliveryDetails: DeliveryDetails): Boolean {
        return dslContext.insertInto(LOGISTICS)
            .set(LOGISTICS.ORDER_ID, deliveryDetails.orderId)
            .set(LOGISTICS.ID, deliveryDetails.orderId)
            .set(LOGISTICS.ESTIMATED_DELIVERY_DATE, deliveryDetails.estimatedDeliveryDate.toLocalDate())
            .set(LOGISTICS.STATUS, deliveryDetails.status.name)
            .set(LOGISTICS.ADDRESS, deliveryDetails.address)
            .set(LOGISTICS.RECIPIENT_NAME, deliveryDetails.recipientName)
            .set(LOGISTICS.RECIPIENT_PHONE, deliveryDetails.recipientPhone)
            .set(LOGISTICS.DELIVERY_NOTES, deliveryDetails.deliveryNotes)
            .set(LOGISTICS.COORDINATES, deliveryDetails.coordinates)
            .set(LOGISTICS.WAREHOUSE_ID, deliveryDetails.warehouseId)
            .execute() > 0
    }

}