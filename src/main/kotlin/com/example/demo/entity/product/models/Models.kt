package com.example.demo.entity.product.models

import java.math.BigDecimal

data class Product(
    val productId: Int?,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val imageUrl: String,
    val supplierId: Int,
    val type: String,
    val pricePerTon: BigDecimal,
    val density: BigDecimal,
    var supplier: Supplier? = null,
)

data class Supplier(
    val supplierId: Int,
    val name: String,
    val contactInfo: String,
    val address: String,
)