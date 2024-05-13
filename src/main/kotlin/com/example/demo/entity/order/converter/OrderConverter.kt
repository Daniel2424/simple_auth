package com.example.demo.entity.order.converter

import com.example.demo.entity.order.models.Order
import com.example.demo.entity.order.models.OrderStatus
import com.example.demo.entity.order.models.Warehouse
import com.yourpackage.generated.Tables
import com.yourpackage.generated.Tables.ORDERS
import org.jooq.Record

object OrderConverter {
    fun convertToOrder(orderRecord: Record): Order =
        Order(
            orderId = orderRecord.get(ORDERS.ORDERID),
            userId = orderRecord.get(ORDERS.USERID),
            orderDate = orderRecord.get(ORDERS.ORDERDATE).toString(),
            status = convertToOrderStatus(orderRecord.get(ORDERS.STATUS)),
            totalPrice = orderRecord.get(ORDERS.TOTALPRICE),
        )
    fun convertToWarehouse(record: Record): Warehouse {
        return Warehouse(
            id = record.get(Tables.WAREHOUSES.ID),
            name = record.get(Tables.WAREHOUSES.NAME),
            address = record.get(Tables.WAREHOUSES.ADDRESS),
            coordinates = record.get(Tables.WAREHOUSES.COORDINATES),
        )
    }

    private fun convertToOrderStatus(statusName: String): OrderStatus = OrderStatus.valueOf(statusName)
}

