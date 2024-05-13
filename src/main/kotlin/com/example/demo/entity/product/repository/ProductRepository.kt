package com.example.demo.entity.product.repository

import com.example.demo.entity.product.models.Product
import com.yourpackage.generated.Tables
import com.yourpackage.generated.Tables.PRODUCTS
import com.yourpackage.generated.tables.Products
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(
    private val dsl: DSLContext,
) {
    fun getAllProducts(): List<Product> {
        val products = dsl.selectFrom(PRODUCTS)
            .fetch()
            .map {
                Product(
                    productId = it[PRODUCTS.PRODUCTID],
                    name = it[PRODUCTS.NAME],
                    description = it[PRODUCTS.DESCRIPTION],
                    price = it[PRODUCTS.PRICE],
                    imageUrl = it[PRODUCTS.IMAGEURL],
                    supplierId = it[PRODUCTS.SUPPLIERID],
                    type = it[PRODUCTS.TYPE],
                    pricePerTon = it[PRODUCTS.PRICEPERTON],
                    density = it[PRODUCTS.DENSITY],
                )
            }
        return products
    }

}