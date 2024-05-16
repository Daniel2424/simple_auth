package com.example.demo.entity.product.repository

import com.example.demo.entity.product.models.Product
import com.example.demo.entity.product.models.Supplier
import com.yourpackage.generated.Tables.PRODUCTS
import com.yourpackage.generated.Tables.SUPPLIERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException

@Repository
class ProductRepository(
    private val dsl: DSLContext,
) {
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    fun getProductById(productId: Int): Product {
        val record = dsl.selectFrom(PRODUCTS)
            .where(PRODUCTS.PRODUCTID.eq(productId))
            .fetchOne() ?: throw RuntimeException("Нет такого продукта")
        return Product(
            productId = record[PRODUCTS.PRODUCTID],
            name = record[PRODUCTS.NAME],
            description = record[PRODUCTS.DESCRIPTION],
            price = record[PRODUCTS.PRICE],
            imageUrl = record[PRODUCTS.IMAGEURL],
            supplierId = record[PRODUCTS.SUPPLIERID],
            type = record[PRODUCTS.TYPE],
            pricePerTon = record[PRODUCTS.PRICEPERTON],
            density = record[PRODUCTS.DENSITY],
        )
    }

    @Transactional(readOnly = true)
    fun getSupplierById(supplierId: Int): Supplier {
        val supplier = dsl.selectFrom(SUPPLIERS)
            .where(SUPPLIERS.SUPPLIERID.eq(supplierId))
            .fetch()
            .map { supplier ->
                Supplier(
                    supplierId = supplier[SUPPLIERS.SUPPLIERID],
                    name = supplier[SUPPLIERS.NAME],
                    address = supplier[SUPPLIERS.ADDRESS],
                    contactInfo = supplier[SUPPLIERS.CONTACTINFO],
                )

            }
            .firstOrNull() ?: throw RuntimeException("Нет поставщика с таким id = $supplierId")

        return supplier
    }

    @Transactional(readOnly = true)
    fun getAllSuppliers(): List<Supplier> {
        return dsl.selectFrom(SUPPLIERS)
            .fetch()
            .map { supplier ->
                Supplier(
                    supplierId = supplier[SUPPLIERS.SUPPLIERID],
                    name = supplier[SUPPLIERS.NAME],
                    address = supplier[SUPPLIERS.ADDRESS],
                    contactInfo = supplier[SUPPLIERS.CONTACTINFO],
                )
            }
    }

    @Transactional
    fun createProduct(product: Product): Int {
        return dsl.insertInto(PRODUCTS)
            .set(PRODUCTS.NAME, product.name)
            .set(PRODUCTS.DESCRIPTION, product.description)
            .set(PRODUCTS.DENSITY, product.density)
            .set(PRODUCTS.PRICE, product.price)
            .set(PRODUCTS.PRICEPERTON, product.pricePerTon)
            .set(PRODUCTS.IMAGEURL, product.imageUrl)
            .set(PRODUCTS.SUPPLIERID, product.supplierId)
            .set(PRODUCTS.TYPE, product.type)
            .execute()

    }

}