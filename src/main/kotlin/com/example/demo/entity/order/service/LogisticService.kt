package com.example.demo.entity.order.service

import com.example.demo.entity.geo.service.GeoService
import com.example.demo.entity.order.models.*
import com.example.demo.entity.order.repository.LogisticsRepository
import com.example.demo.entity.productCart.service.ProductCartService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class LogisticService(
    private val logisticsRepository: LogisticsRepository,
    private val warehouseService: WarehouseService,
    private val truckService: TruckService,
    private val productCartService: ProductCartService,
    private val geoService: GeoService,
) {
    fun createOrderLogistics(order: Order, gdDelivery: GdDelivery) {
        val deliveryDetails = fillFullLogisticData(order, gdDelivery)
        logisticsRepository.createLogistics(deliveryDetails)
    }

    /**
     * Заполняется вся информации о самой доставке в таблице logistics
     */
    private fun fillFullLogisticData(order: Order, gdDelivery: GdDelivery): DeliveryDetails {
        val deliveryDate = gdDelivery.estimatedDeliveryDate
        return DeliveryDetails(
            orderId = order.orderId,
            estimatedDeliveryDate = deliveryDate.atStartOfDay(),
            // actualDeliveryDate = TODO("Когда буду закрывать заказ, надо менять это поле"),
            warehouseId = gdDelivery.warehouseId,
            address = gdDelivery.address,
            coordinates = gdDelivery.coordinates,
            recipientName = gdDelivery.recipientName,
            recipientPhone = gdDelivery.recipientPhone,
            deliveryNotes = gdDelivery.deliveryNotes,
            status = DeliveryStatus.PENDING_PICKUP,
        )
    }

    fun calculateDeliveryCost(userId: Int, deliveryAddress: String): GdDeliveryCostInfo {
        val coordinates = geoService.getCoordinatesByAddress(deliveryAddress)

        val warehouseIdAndDistance = warehouseService.chooseWarehouseAndGetDistance(coordinates)
        val warehouseId = warehouseIdAndDistance.first
        val distance = warehouseIdAndDistance.second


        val estimatedDeliveryDate = calculateEstimatedDeliveryDate(distance)
        val deliveryCost = calculateDeliveryCost(userId, warehouseId, distance)

        return GdDeliveryCostInfo(
            warehouseId = warehouseId,
            distance = distance,
            estimatedDeliveryDate = estimatedDeliveryDate,
            deliveryCost = deliveryCost,
            coordinates = coordinates,
        )
    }

    private fun calculateDeliveryCost(
        userId: Int,
        warehouseId: Int,
        distance: Double,
    ): Double {
        val productCartItems = productCartService.getCartItems(userId)
        val tonOfPetroleumByProductType = productCartService.getWeightOfCartByPetroleumType(productCartItems)
        val trucksForDelivery = truckService.reserveAvailableTruckByWarehouseId(warehouseId, tonOfPetroleumByProductType, false)
        val countTrucks = trucksForDelivery.size

        // Подсчет стоимости доставки
        var deliveryCost: Double = 0.0

        tonOfPetroleumByProductType.forEach{ (fuelType, tonOfProduct) ->
            val costPerKm = when (fuelType) {
                FuelType.GASOLINE -> 10.0
                FuelType.DIESEL -> 12.0
                FuelType.KEROSENE -> 15.0
                FuelType.FUEL_OIL -> 20.0
                FuelType.BITUMEN -> 25.0
                FuelType.CRUDE_OIL -> 30.0
            }
            val fuelCost = costPerKm * distance * tonOfProduct
            deliveryCost += fuelCost
        }

        deliveryCost += countTrucks * 11000 // Стоимость аренды фуры
        deliveryCost += (10 * distance) * countTrucks // стоимость перевозки за каждый километр для всех фур
        return deliveryCost
    }

    /**
     * Примерный расчет дней доставки на основе расстояния
     */
    fun calculateEstimatedDeliveryDate(distance: Double): LocalDate {
        val preparationDays = 2
        val averageSpeed = 80.0 // Средняя скорость доставки на фуре в км/ч
        val daysForDelivery = distance / averageSpeed // Дни доставки

        val totalDays = daysForDelivery + preparationDays // Общее количество дней

        return LocalDate.now().plusDays(totalDays.toLong()) // Предполагаемая дата доставки
    }
}
