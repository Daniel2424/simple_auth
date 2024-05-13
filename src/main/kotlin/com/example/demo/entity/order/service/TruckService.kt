package com.example.demo.entity.order.service

import com.example.demo.entity.order.models.FuelType
import com.example.demo.entity.order.models.Truck
import com.example.demo.entity.order.repository.TruckRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class TruckService(
    private val truckRepository: TruckRepository,
) {
    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun reserveAvailableTruckByWarehouseId(
        warehouseId: Int,
        totalVolumeByFuelType : Map<FuelType, Int>,
        forReserve: Boolean = true,
    ): List<Truck> { // TODO добавить PetroleumType
        val availableTrucks = truckRepository.findAvailableTrucksByWarehouseId(warehouseId)
        // TODO("Можно оптимизировать разделив траки сразу по виду топлива")
        val trucksForReserve = mutableListOf<Truck>()

        totalVolumeByFuelType.forEach { (fuelType, volume) ->
            var remainingVolume = volume
            availableTrucks.filter { FuelType.valueOf(it.productType) == fuelType }.forEach { truck ->
                if (remainingVolume <= 0) return@forEach
                // В одной тонне 1377 литров бензина
                remainingVolume -= truck.capacityLiters / 1377
                trucksForReserve.add(truck)
            }
        }

        if (trucksForReserve.isNotEmpty() && forReserve) {
            truckRepository.reserveTrucks(trucksForReserve)
        }

        return trucksForReserve
    }
}