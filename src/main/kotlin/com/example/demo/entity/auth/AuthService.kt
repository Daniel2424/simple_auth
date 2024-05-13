package com.example.demo.entity.auth

import com.example.demo.entity.user.models.User
import com.example.demo.entity.user.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class AuthService(
    private val userService: UserService,
) {
    fun getAuthUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = if (authentication != null && authentication.isAuthenticated) {
            authentication.name
        } else {
            throw RuntimeException("User not found")
        }
        return userService.findUserByUsername(username)
    }
}