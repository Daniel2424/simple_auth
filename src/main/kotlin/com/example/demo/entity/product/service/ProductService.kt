package com.example.demo.entity.product.service

import com.example.demo.entity.product.models.Product
import com.example.demo.entity.product.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {

    fun getAllProducts(): List<Product> {
        return productRepository.getAllProducts()
    }
}