package com.example.demo.entity.order.controller

import com.example.demo.entity.auth.AuthService
import com.example.demo.entity.order.models.*
import com.example.demo.entity.order.service.LogisticService
import com.example.demo.entity.order.service.OrderService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component


@Component
@GraphQLApi
class OrderController(
    private val authService: AuthService,
    private val orderService: OrderService,
    private val logisticService: LogisticService
) {
    @GraphQLMutation(name = "createOrder")
    fun createOrder(
        @GraphQLArgument(name = "deliveryDetails") gdDelivery: GdDelivery,
    ): Order {
        val user = authService.getAuthUser()
        return orderService.createOrder(gdDelivery, user.id!!)
    }

    @GraphQLQuery(name = "getDeliveryCost")
    fun getDeliveryCost(
        @GraphQLArgument(name = "deliveryAddress") deliveryAddress: String,
    ): GdDeliveryCostInfo {
        val user = authService.getAuthUser()
        return logisticService.calculateDeliveryCost(user.id!!, deliveryAddress)
    }

    @GraphQLQuery(name = "getAllOrders")
    fun getAllOrders(): List<OrderFullInfo> {
        val user = authService.getAuthUser()
        return orderService.getAllOrders(user.id!!)
    }

    @GraphQLQuery(name = "getOrder")
    fun getOrderByOrderId(@GraphQLArgument(name = "orderId") orderId: Int): OrderFullInfo {
        val user = authService.getAuthUser()
        return orderService.getOrderByOrderId(orderId)
    }
}