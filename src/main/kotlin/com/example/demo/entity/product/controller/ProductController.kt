package com.example.demo.entity.product.controller

import com.example.demo.entity.order.models.FuelType
import com.example.demo.entity.product.models.Product
import com.example.demo.entity.product.models.Supplier
import com.example.demo.entity.product.service.ProductService
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component


@Component
@GraphQLApi
class ProductController(
    private val productService: ProductService,
) {

    @GraphQLQuery(name = "getAllProducts")
    fun getAllProducts(): List<Product> {
        return productService.getAllProducts()
    }

    @GraphQLQuery(name = "getAllSuppliers")
    fun getAllSuppliers(): List<Supplier> {
        return productService.getAllSuppliers()
    }

    @GraphQLQuery(name = "getAllFuelTypes")
    fun getAllFuelType(): List<FuelType> {
        return productService.getAllFuelType()
    }


}