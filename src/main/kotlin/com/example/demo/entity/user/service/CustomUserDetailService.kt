package com.example.demo.entity.user.service

import com.example.demo.entity.user.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        // Создание UserDetails используя User.builder() для установки свойств пользователя.
        return User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(user.role)  // Назначение базовых прав (roles)
            .build()
    }
}