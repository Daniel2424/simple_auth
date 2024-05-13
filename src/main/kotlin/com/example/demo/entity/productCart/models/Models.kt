package com.example.demo.entity.productCart.models

import com.example.demo.entity.product.models.Product

data class CartItem(
    val userId: Int,
    val product: Product,
    val quantity: Int
)