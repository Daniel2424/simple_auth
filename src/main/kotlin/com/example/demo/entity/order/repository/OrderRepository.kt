package com.example.demo.entity.order.repository

import com.example.demo.entity.order.converter.OrderConverter.convertToOrder
import com.example.demo.entity.order.models.*
import com.example.demo.entity.product.models.Product
import com.example.demo.entity.productCart.models.CartItem
import com.yourpackage.generated.Tables.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Репозиторий для сохранения данных о заказе в таблицы [ORDERS] и [ORDERDETAILS]
 */
@Repository
class OrderRepository(
    private val dslContext: DSLContext,
) {
    @Transactional
    fun createOrder(userId: Int, totalPrice: BigDecimal): Order? {
        return dslContext.insertInto(ORDERS)
            .set(ORDERS.USERID, userId)
            .set(ORDERS.TOTALPRICE, totalPrice)
            .set(ORDERS.ORDERDATE, LocalDateTime.now())
            .set(ORDERS.STATUS, OrderStatus.PENDING.name)
            .returning()
            .fetch()
            .map { convertToOrder(it) }
            .firstOrNull()
    }

    @Transactional
    fun fillTableOrderTrucks(orderId: Int, reservedTrucks: List<Truck>) {
        reservedTrucks.forEach { truck ->
            dslContext.insertInto(ORDER_TRUCKS)
                .set(ORDER_TRUCKS.ORDER_ID, orderId)
                .set(ORDER_TRUCKS.TRUCK_ID, truck.id)
                .execute()
        }

    }

    @Transactional
    fun createOrderDetails(orderId: Int, cartProduct: List<CartItem>) {
        cartProduct.forEach {
            dslContext.insertInto(ORDERDETAILS)
                .set(ORDERDETAILS.ORDERID, orderId)
                .set(ORDERDETAILS.PRODUCTID, it.product.productId)
                .set(ORDERDETAILS.QUANTITY, it.quantity)
                .set(ORDERDETAILS.UNITPRICE, it.product.pricePerTon)
                .execute()
        }
    }

    @Transactional(readOnly = true)
    fun getAllOrdersByUserId(userId: Int): List<OrderFullInfo> {
        val result = dslContext
            .select()
            .from(ORDERS)
            .innerJoin(ORDERDETAILS).on(ORDERS.ORDERID.eq(ORDERDETAILS.ORDERID))
            .innerJoin(PRODUCTS).on(ORDERDETAILS.PRODUCTID.eq(PRODUCTS.PRODUCTID))
            .innerJoin(LOGISTICS).on(ORDERS.ORDERID.eq(LOGISTICS.ORDER_ID))
            .leftJoin(ORDER_TRUCKS).on(ORDERS.ORDERID.eq(ORDER_TRUCKS.ORDER_ID))
            .leftJoin(TRUCKS).on(ORDER_TRUCKS.TRUCK_ID.eq(TRUCKS.ID))
            .leftJoin(COURIERS).on(TRUCKS.COURIER_ID.eq(COURIERS.ID))
            .where(ORDERS.USERID.eq(userId))
            .fetch()

        val ordersMap = mutableMapOf<Int, Order>()
        val orderDetailsMap = mutableMapOf<Int, MutableList<OrderDetails>>()
        val logisticsMap = mutableMapOf<Int, Logistic>()

        result.forEach { record ->
            val orderId = record[ORDERS.ORDERID]

            if (!ordersMap.containsKey(orderId)) {
                val orderRecord = record.into(ORDERS)
                val order = Order(
                    orderId = orderRecord[ORDERS.ORDERID],
                    userId = orderRecord[ORDERS.USERID],
                    orderDate = orderRecord[ORDERS.ORDERDATE].toLocalDate().toString(),
                    totalPrice = orderRecord[ORDERS.TOTALPRICE],
                    status = OrderStatus.valueOf(orderRecord[ORDERS.STATUS])
                )
                ordersMap[orderId] = order
            }

            val productRecord = record.into(PRODUCTS)
            val product = Product(
                productId = productRecord[PRODUCTS.PRODUCTID],
                name = productRecord[PRODUCTS.NAME],
                description = productRecord[PRODUCTS.DESCRIPTION],
                price = productRecord[PRODUCTS.PRICE],
                pricePerTon = productRecord[PRODUCTS.PRICEPERTON],
                type = productRecord[PRODUCTS.TYPE],
                imageUrl = productRecord[PRODUCTS.IMAGEURL],
                supplierId = productRecord[PRODUCTS.SUPPLIERID],
                density = productRecord[PRODUCTS.DENSITY]
            )

            val orderDetailRecord = record.into(ORDERDETAILS)
            val orderDetail = OrderDetails(
                quantity = orderDetailRecord[ORDERDETAILS.QUANTITY],
                unitPrice = orderDetailRecord[ORDERDETAILS.UNITPRICE],
                product = product
            )

            if (!orderDetailsMap.containsKey(orderId)) {
                orderDetailsMap[orderId] = mutableListOf()
            }
            orderDetailsMap[orderId]?.add(orderDetail)

            val logisticRecord = record.into(LOGISTICS)
            val truckRecord = record.into(TRUCKS)
            val courierRecord = record.into(COURIERS)

            val truck = TruckFUllInfo(
                truckId = truckRecord[TRUCKS.ID],
                capacityLiters = truckRecord[TRUCKS.CAPACITY_LITERS],
                productType = truckRecord[TRUCKS.PRODUCT_TYPE],
                isAvailable = truckRecord[TRUCKS.IS_AVAILABLE],
                warehouseId = truckRecord[TRUCKS.WAREHOUSE_ID],
                courier = Courier(
                    courierId = courierRecord[COURIERS.ID],
                    name = courierRecord[COURIERS.NAME],
                    phone = courierRecord[COURIERS.PHONE],
                    email = courierRecord[COURIERS.EMAIL]
                )
            )

            if (!logisticsMap.containsKey(orderId)) {
                val logistic = Logistic(
                    logisticId = logisticRecord[LOGISTICS.ID],
                    orderId = logisticRecord[LOGISTICS.ORDER_ID],
                    estimatedDeliveryDate = logisticRecord[LOGISTICS.ESTIMATED_DELIVERY_DATE].atStartOfDay(),
                    address = logisticRecord[LOGISTICS.ADDRESS],
                    recipientName = logisticRecord[LOGISTICS.RECIPIENT_NAME],
                    recipientPhone = logisticRecord[LOGISTICS.RECIPIENT_PHONE],
                    deliveryNotes = logisticRecord[LOGISTICS.DELIVERY_NOTES],
                    warehouseId = logisticRecord[LOGISTICS.WAREHOUSE_ID],
                    coordinates = logisticRecord[LOGISTICS.COORDINATES],
                    status = logisticRecord[LOGISTICS.STATUS],
                    trucks = mutableListOf(truck)
                )
                logisticsMap[orderId] = logistic
            } else {
                val trucks = logisticsMap[orderId]?.trucks?.map{ it.truckId } ?: emptyList()
                if (!trucks.contains(truck.truckId)) {
                    logisticsMap[orderId]?.trucks?.add(truck)
                }
            }
        }

        return ordersMap.map { (_, order) ->
            val orderId = order.orderId
            OrderFullInfo(
                order = order,
                orderDetails = orderDetailsMap[orderId] ?: emptyList(),
                logistic = logisticsMap[orderId]
            )
        }
    }

    @Transactional(readOnly = true)
    fun getOrderDetailsByOrderId(orderId: Int): OrderFullInfo? {
        val result = dslContext
            .select()
            .from(ORDERS)
            .innerJoin(ORDERDETAILS).on(ORDERS.ORDERID.eq(ORDERDETAILS.ORDERID))
            .innerJoin(PRODUCTS).on(ORDERDETAILS.PRODUCTID.eq(PRODUCTS.PRODUCTID))
            .innerJoin(LOGISTICS).on(ORDERS.ORDERID.eq(LOGISTICS.ORDER_ID))
            .leftJoin(ORDER_TRUCKS).on(ORDERS.ORDERID.eq(ORDER_TRUCKS.ORDER_ID))
            .leftJoin(TRUCKS).on(ORDER_TRUCKS.TRUCK_ID.eq(TRUCKS.ID))
            .leftJoin(COURIERS).on(TRUCKS.COURIER_ID.eq(COURIERS.ID))
            .where(ORDERS.ORDERID.eq(orderId))
            .fetch()

        val ordersMap = mutableMapOf<Int, Order>()
        val orderDetailsMap = mutableMapOf<Int, MutableList<OrderDetails>>()
        val logisticsMap = mutableMapOf<Int, Logistic>()

        result.forEach { record ->
            val orderId = record[ORDERS.ORDERID]

            if (!ordersMap.containsKey(orderId)) {
                val orderRecord = record.into(ORDERS)
                val order = Order(
                    orderId = orderRecord[ORDERS.ORDERID],
                    userId = orderRecord[ORDERS.USERID],
                    orderDate = orderRecord[ORDERS.ORDERDATE].toLocalDate().toString(),
                    totalPrice = orderRecord[ORDERS.TOTALPRICE],
                    status = OrderStatus.valueOf(orderRecord[ORDERS.STATUS])
                )
                ordersMap[orderId] = order
            }

            val productRecord = record.into(PRODUCTS)
            val product = Product(
                productId = productRecord[PRODUCTS.PRODUCTID],
                name = productRecord[PRODUCTS.NAME],
                description = productRecord[PRODUCTS.DESCRIPTION],
                price = productRecord[PRODUCTS.PRICE],
                pricePerTon = productRecord[PRODUCTS.PRICEPERTON],
                type = productRecord[PRODUCTS.TYPE],
                imageUrl = productRecord[PRODUCTS.IMAGEURL],
                supplierId = productRecord[PRODUCTS.SUPPLIERID],
                density = productRecord[PRODUCTS.DENSITY]
            )

            val orderDetailRecord = record.into(ORDERDETAILS)
            val orderDetail = OrderDetails(
                quantity = orderDetailRecord[ORDERDETAILS.QUANTITY],
                unitPrice = orderDetailRecord[ORDERDETAILS.UNITPRICE],
                product = product
            )

            if (!orderDetailsMap.containsKey(orderId)) {
                orderDetailsMap[orderId] = mutableListOf()
            }
            orderDetailsMap[orderId]?.add(orderDetail)

            val logisticRecord = record.into(LOGISTICS)
            val truckRecord = record.into(TRUCKS)
            val courierRecord = record.into(COURIERS)

            val truck = TruckFUllInfo(
                truckId = truckRecord[TRUCKS.ID],
                capacityLiters = truckRecord[TRUCKS.CAPACITY_LITERS],
                productType = truckRecord[TRUCKS.PRODUCT_TYPE],
                isAvailable = truckRecord[TRUCKS.IS_AVAILABLE],
                warehouseId = truckRecord[TRUCKS.WAREHOUSE_ID],
                courier = Courier(
                    courierId = courierRecord[COURIERS.ID],
                    name = courierRecord[COURIERS.NAME],
                    phone = courierRecord[COURIERS.PHONE],
                    email = courierRecord[COURIERS.EMAIL]
                )
            )

            if (!logisticsMap.containsKey(orderId)) {
                val logistic = Logistic(
                    logisticId = logisticRecord[LOGISTICS.ID],
                    orderId = logisticRecord[LOGISTICS.ORDER_ID],
                    estimatedDeliveryDate = logisticRecord[LOGISTICS.ESTIMATED_DELIVERY_DATE].atStartOfDay(),
                    address = logisticRecord[LOGISTICS.ADDRESS],
                    recipientName = logisticRecord[LOGISTICS.RECIPIENT_NAME],
                    recipientPhone = logisticRecord[LOGISTICS.RECIPIENT_PHONE],
                    deliveryNotes = logisticRecord[LOGISTICS.DELIVERY_NOTES],
                    warehouseId = logisticRecord[LOGISTICS.WAREHOUSE_ID],
                    coordinates = logisticRecord[LOGISTICS.COORDINATES],
                    status = logisticRecord[LOGISTICS.STATUS],
                    trucks = mutableListOf(truck)
                )
                logisticsMap[orderId] = logistic
            } else {
                val trucks = logisticsMap[orderId]?.trucks?.map{ it.truckId } ?: emptyList()
                if (!trucks.contains(truck.truckId)) {
                    logisticsMap[orderId]?.trucks?.add(truck)
                }
            }
        }

        val order = orderDetailsMap.keys.firstOrNull()?.let { orderId ->
            val orderRecord = result.first().into(ORDERS)
            Order(
                orderId = orderRecord[ORDERS.ORDERID],
                userId = orderRecord[ORDERS.USERID],
                orderDate = orderRecord[ORDERS.ORDERDATE].toLocalDate().toString(),
                totalPrice = orderRecord[ORDERS.TOTALPRICE],
                status = OrderStatus.valueOf(orderRecord[ORDERS.STATUS])
            )
        }

        return order?.let { OrderFullInfo(order = it, orderDetails = orderDetailsMap[orderId] ?: emptyList(), logistic = logisticsMap[orderId]) }
    }
}




