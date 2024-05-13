package com.example.demo.entity.admin.controller

import com.example.demo.entity.auth.AuthService
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component

@GraphQLApi
@Component
class AdminController(
    private val authService: AuthService,
) {
    fun getAdminData() {
        val user = authService.getAuthUser()
        // if (user.)

            TODO()
    }
}