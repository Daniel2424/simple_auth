package com.example.demo.entity.order.repository

import com.example.demo.entity.order.models.Courier
import com.example.demo.entity.order.models.Truck
import com.sun.xml.bind.v2.model.runtime.RuntimeClassInfo
import com.yourpackage.generated.Tables
import com.yourpackage.generated.Tables.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException

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

    @Transactional(readOnly = true)
    fun getTrucksByOrderId(orderId: Int): List<Truck> {
        val trucksIds = dslContext
            .selectFrom(ORDER_TRUCKS)
            .where(ORDER_TRUCKS.ORDER_ID.eq(orderId))
            .fetch()
            .map { it[ORDER_TRUCKS.TRUCK_ID] }

        return dslContext.selectFrom(TRUCKS)
            .where(TRUCKS.ID.`in`(trucksIds))
            .fetchInto(Truck::class.java)
    }

    @Transactional(readOnly = true)
    fun gatCourierById(courierId: Int): Courier {
        val courierRecord = dslContext
            .selectFrom(COURIERS)
            .where(COURIERS.ID.eq(courierId))
            .fetchOne() ?: throw RuntimeException("Нет курьерас таким id = $courierId")

        return Courier(
            courierId = courierRecord.get(COURIERS.ID),
            name = courierRecord.get(COURIERS.NAME),
            phone = courierRecord.get(COURIERS.PHONE),
            email = courierRecord.get(COURIERS.EMAIL),
        )


    }

}