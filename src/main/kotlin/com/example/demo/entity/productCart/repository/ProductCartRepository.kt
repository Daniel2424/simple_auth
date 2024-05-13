package com.example.demo.entity.productCart.repository

import com.example.demo.entity.order.models.FuelType
import com.example.demo.entity.product.models.Product
import com.example.demo.entity.productCart.models.CartItem
import com.yourpackage.generated.Tables
import com.yourpackage.generated.Tables.CART
import com.yourpackage.generated.Tables.PRODUCTS
import com.yourpackage.generated.tables.Cart
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProductCartRepository(
    private val dslContext: DSLContext,
) {

    @Transactional
    fun addToCart(userId: Int, productId: Int, quantity: Int): List<CartItem> {
        dslContext.insertInto(CART)
            .set(CART.USER_ID, userId)
            .set(CART.PRODUCT_ID, productId)
            .set(CART.QUANTITY, quantity)
            .onConflict(CART.USER_ID, CART.PRODUCT_ID)
            .doUpdate()
            .set(CART.QUANTITY, CART.QUANTITY.plus(quantity))
            .execute()

        return getCartItems(userId)
    }

    @Transactional
    fun decreaseQuantityInCart(userId: Int, productId: Int, quantity: Int): List<CartItem> {
        val currentQuantity = dslContext.select(CART.QUANTITY)
            .from(CART)
            .where(CART.USER_ID.eq(userId).and(CART.PRODUCT_ID.eq(productId)))
            .fetchOne(CART.QUANTITY)

        if (currentQuantity == null) {
            throw IllegalArgumentException("CartItem not found for user $userId and product $productId")
        }

        val newQuantity = currentQuantity - quantity

        if (newQuantity <= 0) {
            return removeFromCart(userId, productId)
        } else {
            dslContext.update(CART)
                .set(CART.QUANTITY, newQuantity)
                .where(CART.USER_ID.eq(userId).and(CART.PRODUCT_ID.eq(productId)))
                .execute()
        }
        return getCartItems(userId)
    }

    @Transactional
    fun updateCart(userId: Int, productId: Int, quantity: Int): List<CartItem> {
        dslContext.update(CART)
            .set(CART.QUANTITY, quantity)
            .where(CART.USER_ID.eq(userId).and(CART.PRODUCT_ID.eq(productId)))
            .execute()

        return getCartItems(userId)
    }

    @Transactional
    fun removeFromCart(userId: Int, productId: Int): List<CartItem> {
        dslContext.deleteFrom(CART)
            .where(CART.USER_ID.eq(userId).and(CART.PRODUCT_ID.eq(productId)))
            .execute()
        return getCartItems(userId)
    }



    @Transactional(readOnly = true)
    fun getCartItems(userId: Int): List<CartItem> {
        return dslContext.select()
            .from(CART)
            .join(PRODUCTS).on(CART.PRODUCT_ID.eq(PRODUCTS.PRODUCTID))
            .where(CART.USER_ID.eq(userId))
            .fetch { record ->
                CartItem(
                    userId = record[CART.USER_ID],
                    product = Product(
                        productId = record[PRODUCTS.PRODUCTID],
                        name = record[PRODUCTS.NAME],
                        description = record[PRODUCTS.DESCRIPTION],
                        price = record[PRODUCTS.PRICE],
                        imageUrl = record[PRODUCTS.IMAGEURL],
                        type = record[PRODUCTS.TYPE],
                        pricePerTon = record[PRODUCTS.PRICEPERTON],
                        density = record[PRODUCTS.DENSITY],
                        supplierId = record[PRODUCTS.SUPPLIERID]
                    ),
                    quantity = record[CART.QUANTITY]
                )
            }
            .sortedBy {
                it.product.productId
            }
    }

    @Transactional(readOnly = true)
    fun getTotalPrice(userId: Int): BigDecimal {
        return dslContext.select(CART.QUANTITY.multiply(PRODUCTS.PRICEPERTON).sum())
            .from(CART)
            .join(PRODUCTS).on(CART.PRODUCT_ID.eq(PRODUCTS.PRODUCTID))
            .where(CART.USER_ID.eq(userId))
            .fetchOne(0, BigDecimal::class.java) ?: BigDecimal.ZERO
    }

    @Transactional
    fun clearProductCart(userId: Int) {
        dslContext.deleteFrom(CART)
            .where(CART.USER_ID.eq(userId))
            .execute()
    }

    @Transactional(readOnly = true)
    fun getCountProductInCart(userId: Int): Int {
        val countProductsInCart = dslContext.select(CART.USER_ID.count())
            .from(CART)
            .where(CART.USER_ID.eq(userId))
            .fetchOne(0, Int::class.java) ?: 0

        return countProductsInCart
    }
}