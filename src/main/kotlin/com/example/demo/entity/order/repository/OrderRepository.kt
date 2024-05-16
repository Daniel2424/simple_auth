package com.example.demo.entity.order.repository

import com.example.demo.entity.order.converter.OrderConverter.convertToOrder
import com.example.demo.entity.order.models.*
import com.example.demo.entity.product.repository.ProductRepository
import com.example.demo.entity.productCart.models.CartItem
import com.yourpackage.generated.Tables.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Репозиторий для сохранения данных о заказе в таблицы [ORDERS] и [ORDERDETAILS]
 */
@Repository
class OrderRepository(
    private val dslContext: DSLContext,
    private val productRepository: ProductRepository,
    private val truckRepository: TruckRepository,
    private val warehouseRepository: WarehouseRepository,
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
    fun getOrderDetailsByOrderId(orderId: Int): OrderFullInfo? {
        val order = dslContext
            .select()
            .from(ORDERS)
            .where(ORDERS.ORDERID.eq(orderId))
            .fetch()
            .map { convertToOrder(it) }
            .firstOrNull() ?: throw RuntimeException("Нет заказа с таким id = $orderId")

        return getOrderFullInfo(order)
    }

    @Transactional(readOnly = true)
    fun getAllOrdersByUserId(userId: Int): List<OrderFullInfo> {
        val orders = dslContext
            .select()
            .from(ORDERS)
            .where(ORDERS.USERID.eq(userId))
            .fetch()
            .map { convertToOrder(it) }



        return orders.map { order ->
            getOrderFullInfo(order)
        }
    }

    private fun getOrderFullInfo(order: Order): OrderFullInfo {
        val orderId = order.orderId
        val orderDetails = dslContext
            .selectFrom(ORDERDETAILS)
            .where(ORDERDETAILS.ORDERID.eq(orderId))
            .fetch()
            .map {
                val productId = it.get(ORDERDETAILS.PRODUCTID)
                val quantity = it.get(ORDERDETAILS.QUANTITY)
                val unitPrice = it.get(ORDERDETAILS.UNITPRICE)
                val product = productRepository.getProductById(productId)
                val fullProduct = product.apply { supplier = productRepository.getSupplierById(product.supplierId) }
                OrderDetails(
                    quantity = quantity,
                    unitPrice = unitPrice,
                    product = fullProduct,
                )
            }
        val logisticRecord = dslContext.selectFrom(LOGISTICS)
            .where(LOGISTICS.ORDER_ID.eq(orderId))
            .fetchOne()
            ?: throw RuntimeException("Запись в таблице Logistics не может быть пустая для orderId = $orderId")
        val trucks = truckRepository.getTrucksByOrderId(orderId)

        val trucksFullInfo = trucks.map { truck ->
            val courier = truckRepository.gatCourierById(truck.courierId)
            TruckFUllInfo(
                truckId = truck.id,
                capacityLiters = truck.capacityLiters,
                productType = truck.productType,
                isAvailable = truck.isAvailable,
                warehouseId = truck.warehouseId,
                courier = courier,
            )
        }

        val warehouseId = logisticRecord.get(LOGISTICS.WAREHOUSE_ID)
        val warehouse = warehouseRepository.getWarehouseById(warehouseId)

        val logistic = Logistic(
            logisticId = logisticRecord.get(LOGISTICS.ID),
            orderId = orderId,
            estimatedDeliveryDate = logisticRecord.get(LOGISTICS.ESTIMATED_DELIVERY_DATE).atStartOfDay(),
            address = logisticRecord.get(LOGISTICS.ADDRESS),
            recipientPhone = logisticRecord.get(LOGISTICS.RECIPIENT_PHONE),
            recipientName = logisticRecord.get(LOGISTICS.RECIPIENT_NAME),
            deliveryNotes = logisticRecord.get(LOGISTICS.DELIVERY_NOTES),
            warehouse = warehouse,
            coordinates = logisticRecord.get(LOGISTICS.COORDINATES),
            status = logisticRecord.get(LOGISTICS.STATUS),
            trucks = trucksFullInfo.toMutableList()
        )

        return OrderFullInfo(
            order = order,
            orderDetails = orderDetails,
            logistic = logistic
        )
    }
}




