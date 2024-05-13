package com.example.demo.entity.order.service

import com.example.demo.entity.order.repository.WarehouseRepository
import org.springframework.stereotype.Service
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Service
class WarehouseService(
    private val warehouseRepository: WarehouseRepository,
) {
    /**
     * Выбор наиближайшего склада для доставки и подсчет расстояние до пункта доставки
     */
    fun chooseWarehouseAndGetDistance(coordinates: String): Pair<Int, Double> {
        val warehouses = warehouseRepository.getAllWarehouses()
        val (lat, lon) = coordinates.split(",").map { it.trim().toDouble() }

        var minDistance = Double.MAX_VALUE
        var closestWarehouseId = -1

        for (warehouse in warehouses) {
            val (warehouseLat, warehouseLon) = warehouse.coordinates.split(",").map { it.trim().toDouble() }
            val distance = calculateDistance(lat, lon, warehouseLat, warehouseLon)

            if (distance < minDistance) {
                minDistance = distance
                closestWarehouseId = warehouse.id
            }
        }

        return closestWarehouseId to minDistance
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}