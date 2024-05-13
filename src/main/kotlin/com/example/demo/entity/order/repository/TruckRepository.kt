package com.example.demo.entity.order.repository

import com.example.demo.entity.order.models.Truck
import com.yourpackage.generated.Tables
import com.yourpackage.generated.Tables.TRUCKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
class TruckRepository(
    private val dslContext: DSLContext,
) {
    @Transactional
    fun findAvailableTrucksByWarehouseId(warehouseId: Int): List<Truck> {
        return dslContext.selectFrom(TRUCKS)
            .where(TRUCKS.WAREHOUSE_ID.eq(warehouseId))
            .and(TRUCKS.IS_AVAILABLE.eq(true))
            .fetchInto(Truck::class.java)
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun reserveTrucks(trucks: List<Truck>) {
        val trucksIds = trucks.map { it.id }
        val updatedRows = dslContext.update(TRUCKS)
            .set(TRUCKS.IS_AVAILABLE, false)
            .where(TRUCKS.ID.`in`(trucksIds).and(TRUCKS.IS_AVAILABLE.eq(true)))
            .execute()

        if (updatedRows < trucks.size) {
            throw IllegalStateException("Not all trucks were available for reservation")
        }
    }

}