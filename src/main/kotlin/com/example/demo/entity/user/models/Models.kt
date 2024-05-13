package com.example.demo.entity.user.models

import java.util.*

data class UserDto(
    val username: String,
    val password: String,
)

data class User(
    val id: Int? = null,
    val username: String,
    val password: String? = null,
)

data class JwtRequest(
    val username: String,
    val password: String,
)

data class JwtResponse(
    val jwtToken: String,
)

data class Post(val id: UUID, val title: String)