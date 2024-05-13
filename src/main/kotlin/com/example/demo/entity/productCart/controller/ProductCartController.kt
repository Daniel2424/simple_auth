package com.example.demo.entity.productCart.controller

import com.example.demo.entity.auth.AuthService
import com.example.demo.entity.productCart.models.CartItem
import com.example.demo.entity.productCart.service.ProductCartService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class ProductCartController(
    private val authService: AuthService,
    private val productCartService: ProductCartService
) {
    @GraphQLMutation(name = "addToCart")
    fun addToCart(
        @GraphQLArgument(name = "productId") productId: Int,
        @GraphQLArgument(name = "quantity") quantity: Int
    ): List<CartItem> {
        val user = authService.getAuthUser()
        return productCartService.addToCart(user.id!!, productId, quantity)
    }

    @GraphQLMutation(name = "decreaseQuantityInCart")
    fun decreaseQuantityInCart(
        @GraphQLArgument(name = "productId") productId: Int,
        @GraphQLArgument(name = "quantity") quantity: Int
    ): List<CartItem> {
        val user = authService.getAuthUser()
        return productCartService.decreaseQuantityInCart(user.id!!, productId, quantity)
    }

    @GraphQLMutation(name = "updateCart")
    fun updateCart(
        @GraphQLArgument(name = "productId") productId: Int,
        @GraphQLArgument(name = "quantity") quantity: Int
    ): List<CartItem> {
        val user = authService.getAuthUser()
        return productCartService.updateCart(user.id!!, productId, quantity)
    }

    @GraphQLMutation(name = "removeFromCart")
    fun removeFromCart(
        @GraphQLArgument(name = "productId") productId: Int
    ): List<CartItem> {
        val user = authService.getAuthUser()
        return productCartService.removeFromCart(user.id!!, productId)
    }

    @GraphQLQuery(name = "getCartItems")
    fun getCartItems(): List<CartItem> {
        val user = authService.getAuthUser()
        return productCartService.getCartItems(user.id!!)
    }

    @GraphQLQuery(name = "getCountProductInCart")
    fun getCountProductInCart(): Int {
        val user = authService.getAuthUser()
        return productCartService.getCountProductInCart(user.id!!)
    }
}