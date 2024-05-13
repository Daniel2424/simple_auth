package com.example.demo.entity.order.repository

import com.example.demo.entity.order.converter.OrderConverter.convertToWarehouse
import com.example.demo.entity.order.models.Warehouse
import com.yourpackage.generated.Tables.WAREHOUSES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class WarehouseRepository(
    private val dslContext: DSLContext
) {
    fun getAllWarehouses(): List<Warehouse> {
        return dslContext.selectFrom(WAREHOUSES)
            .fetch()
            .map { record -> convertToWarehouse(record) }
    }


}