package com.example.demo.entity.order.models

import com.example.demo.entity.product.models.Product
import java.math.BigDecimal
import java.time.LocalDateTime

enum class OrderStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELLED,
}

data class Order(
    val orderId: Int,
    val userId: Int,
    val orderDate: String,
    val status: OrderStatus,
    val totalPrice: BigDecimal,
)

data class OrderDetails(
    val quantity: Int,
    val unitPrice: BigDecimal,
    val product: Product,
)

data class OrderFullInfo(
    val order: Order,
    val orderDetails: List<OrderDetails>,
    val logistic: Logistic?,
)


data class Logistic(
    val logisticId: Int,
    val orderId: Int,
    val estimatedDeliveryDate: LocalDateTime,
    val address: String,
    val recipientName: String,
    val recipientPhone: String,
    val deliveryNotes: String,
    val warehouseId: Int,
    val coordinates: String,
    val status: String,
    val trucks: MutableList<TruckFUllInfo> = mutableListOf()
)

data class Warehouse(
    val id: Int,
    val name: String,
    val address: String,
    val coordinates: String,
)


// truck
data class Truck(
    val id: Int,
    val capacityLiters: Int,
    val productType: String,
    val isAvailable: Boolean,
    val warehouseId: Int,
    val courierId: Int,
)

data class TruckFUllInfo(
    val truckId: Int,
    val capacityLiters: Int,
    val productType: String,
    val isAvailable: Boolean,
    val warehouseId: Int,
    val courier: Courier,
)

data class Courier(
    val courierId: Int,
    val name: String,
    val phone: String,
    val email: String
)


enum class FuelType {
    GASOLINE,
    DIESEL,
    KEROSENE,
    FUEL_OIL,
    BITUMEN,
    CRUDE_OIL
}