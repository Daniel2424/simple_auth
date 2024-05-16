package com.example.demo.entity.product.service

import com.example.demo.entity.order.models.FuelType
import com.example.demo.entity.product.models.Product
import com.example.demo.entity.product.models.Supplier
import com.example.demo.entity.product.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {

    fun getAllProducts(): List<Product> {
        return productRepository.getAllProducts()
    }

    fun getAllSuppliers(): List<Supplier> {
        return productRepository.getAllSuppliers()
    }

    fun createProduct(product: Product): String {
        val isCreated = productRepository.createProduct(product)
        if (isCreated > 0) return "Продукт создан с"

        return "Ошибка при создании нового продукта"
    }

    fun getAllFuelType(): List<FuelType> {
        return FuelType.entries
    }
}