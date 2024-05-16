package com.example.demo.entity.user.models

import java.util.*

data class UserDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String,
    val role: String? = null,
)

data class User(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String? = null,
    val role: String? = null,
)

data class JwtRequest(
    val username: String,
    val password: String,
)

data class JwtResponse(
    val jwtToken: String,
)

data class Post(val id: UUID, val title: String)