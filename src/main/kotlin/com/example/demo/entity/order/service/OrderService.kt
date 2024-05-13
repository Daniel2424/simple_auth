package com.example.demo.entity.order.service

import com.example.demo.entity.order.models.GdDelivery
import com.example.demo.entity.order.models.Order
import com.example.demo.entity.order.models.OrderFullInfo
import com.example.demo.entity.order.repository.OrderRepository
import com.example.demo.entity.productCart.service.ProductCartService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException
import java.math.BigDecimal

@Service
class OrderService(
    private val logisticsService: LogisticService,
    private val orderRepository: OrderRepository,
    private val productCartService: ProductCartService,
    private val truckService: TruckService,
) {
    // TODO("Нужна нормальная валидация, и что-то возвращать на фронте, в случае ошибок")
    @Transactional
    fun createOrder(gdDelivery: GdDelivery, userId: Int): Order {
        val warehouseId = gdDelivery.warehouseId
        val productCartItems = productCartService.getCartItems(userId)
        if (productCartItems.isEmpty()) throw RuntimeException("Корзина пустая, создать заказа нельзя")
        val tonOfPetroleumByProductType = productCartService.getWeightOfCartByPetroleumType(productCartItems)


        // TODO("Нужно на фронт что-то возвращать, а не 500 кидать")
        val reservedTrucks = truckService.reserveAvailableTruckByWarehouseId(warehouseId, tonOfPetroleumByProductType)
            ?: throw RuntimeException("Нет фур с данного склада, заказ оформить нельзя")
        if (reservedTrucks.isEmpty()) throw RuntimeException("Нет свободных фур с данного склада, которые могли бы доставить заказ")

        // Подсчет общей суммы заказа
        val totalPriceCart = getTotalPrice(userId, gdDelivery.deliveryCost)

        // Тут идет заполнение таблиц orders и order_details
        val order = orderRepository.createOrder(userId, totalPriceCart)
            ?: throw RuntimeException("Не удалось создать заказ!")

        val orderId = order.orderId
        // Заполнение таблиц, связанных с таблице orders
        orderRepository.createOrderDetails(orderId, productCartItems)
        orderRepository.fillTableOrderTrucks(orderId, reservedTrucks)
        logisticsService.createOrderLogistics(order, gdDelivery)

        productCartService.clearProductCart(userId)

        return order
    }

    private fun getTotalPrice(userId: Int, deliveryCost: Double): BigDecimal {
        val totalCartPrice = productCartService.getTotalPrice(userId)
        return totalCartPrice.add(BigDecimal(deliveryCost))
    }

    fun getAllOrders(userId: Int): List<OrderFullInfo> {
        return orderRepository.getAllOrdersByUserId(userId)
    }

    fun getOrderByOrderId(orderId: Int): OrderFullInfo {
        val order = orderRepository.getOrderDetailsByOrderId(orderId)
        if (order == null) {
            TODO()
        }
        return order
    }
}