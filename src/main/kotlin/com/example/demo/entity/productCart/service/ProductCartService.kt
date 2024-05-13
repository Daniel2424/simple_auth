package com.example.demo.entity.productCart.service

import com.example.demo.entity.order.models.FuelType
import com.example.demo.entity.productCart.models.CartItem
import com.example.demo.entity.productCart.repository.ProductCartRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProductCartService(
    private val productCartRepository: ProductCartRepository,
) {
    fun addToCart(userId: Int, productId: Int, quantity: Int): List<CartItem> {
        return productCartRepository.addToCart(userId, productId, quantity)
    }

    fun decreaseQuantityInCart(userId: Int, productId: Int, quantity: Int): List<CartItem> {
        return productCartRepository.decreaseQuantityInCart(userId, productId, quantity)
    }

    fun updateCart(userId: Int, productId: Int, quantity: Int): List<CartItem> {
        return productCartRepository.updateCart(userId, productId, quantity)
    }

    fun removeFromCart(userId: Int, productId: Int): List<CartItem> {
        return productCartRepository.removeFromCart(userId, productId)
    }

    fun getCartItems(userId: Int): List<CartItem> {
        return productCartRepository.getCartItems(userId)
    }

    fun clearProductCart(userId: Int) {
        productCartRepository.clearProductCart(userId)
    }

    fun getTotalPrice(userId: Int): BigDecimal {
        return productCartRepository.getTotalPrice(userId)
    }

    fun getWeightOfCartByPetroleumType(cartItems: List<CartItem>): Map<FuelType, Int> {
        val groupedItems = cartItems.groupBy { FuelType.valueOf(it.product.type) }
        return groupedItems.mapValues { (_, products) ->
            products.sumOf { it.quantity }
        }
    }

    fun getCountProductInCart(userId: Int): Int {
        return productCartRepository.getCountProductInCart(userId)
    }
}