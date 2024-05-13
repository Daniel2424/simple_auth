package com.example.demo.entity.order.models

import java.time.LocalDate
import java.time.LocalDateTime

data class GdDeliveryCostInfo(
    val warehouseId: Int,
    val distance: Double,
    val estimatedDeliveryDate: LocalDate,
    val deliveryCost: Double,
    val coordinates: String,
)

data class GdDelivery(
    val address: String,
    val coordinates: String,
    val recipientName: String,
    val recipientPhone: String,
    val deliveryNotes: String?,
    val deliveryCost: Double,
    val warehouseId: Int,
    val distance: Double,
    val estimatedDeliveryDate: LocalDate, // TODO("Херня какая-то, исправить")
)

data class DeliveryDetails(
    val orderId: Int,
    val estimatedDeliveryDate: LocalDateTime,
    val actualDeliveryDate: LocalDateTime? = null,
    val warehouseId: Int,
    val address: String,
    val coordinates: String,
    val recipientName: String,
    val recipientPhone: String,
    val deliveryNotes: String?,
    val status: DeliveryStatus,
)

enum class DeliveryStatus {
    PENDING_PICKUP,
    IN_TRANSIT,
    DELIVERED,
    DELAYED,
    CANCELLED
}

data class GdDeliveryAddress(
    val address: String,
)

