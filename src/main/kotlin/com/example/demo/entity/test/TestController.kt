package com.example.demo.entity.test

import com.example.demo.entity.auth.AuthService
import com.example.demo.entity.order.repository.OrderRepository
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller

@Component
@GraphQLApi
class SomeCtlskfd(
    private val orderRepository: OrderRepository,
    private val authService: AuthService,
) {
    @GraphQLQuery(name = "test")
    fun test(): Int {
        val user = authService.getAuthUser()
        orderRepository.getAllOrdersByUserId(user.id!!)
        return 5
    }
}