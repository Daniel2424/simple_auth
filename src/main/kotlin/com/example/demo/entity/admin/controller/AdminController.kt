package com.example.demo.entity.admin.controller

import com.example.demo.entity.auth.AuthService
import com.example.demo.entity.product.models.Product
import com.example.demo.entity.product.repository.ProductRepository
import com.example.demo.entity.product.service.ProductService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component

@GraphQLApi
@Component
class AdminController(
    private val authService: AuthService,
    private val productService: ProductService,
) {


    @GraphQLMutation(name = "createProduct")
    fun createProduct(@GraphQLArgument(name = "product") product: Product): String {
        return productService.createProduct(product)
    }
}