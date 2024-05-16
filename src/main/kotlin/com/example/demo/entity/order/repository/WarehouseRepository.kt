package com.example.demo.entity.order.repository

import com.example.demo.entity.order.converter.OrderConverter.convertToWarehouse
import com.example.demo.entity.order.models.Warehouse
import com.yourpackage.generated.Tables.WAREHOUSES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException

@Repository
class WarehouseRepository(
    private val dslContext: DSLContext
) {
    @Transactional(readOnly = true)
    fun getAllWarehouses(): List<Warehouse> {
        return dslContext.selectFrom(WAREHOUSES)
            .fetch()
            .map { record -> convertToWarehouse(record) }
    }

    @Transactional(readOnly = true)
    fun getWarehouseById(warehouseId: Int): Warehouse {
        val warehouseRecord =  dslContext.selectFrom(WAREHOUSES)
            .where(WAREHOUSES.ID.eq(warehouseId))
            .fetchOne() ?: throw RuntimeException("Нет склада с таким id = $warehouseId")

        return convertToWarehouse(warehouseRecord)
    }


}