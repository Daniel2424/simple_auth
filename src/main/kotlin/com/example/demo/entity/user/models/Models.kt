package com.example.demo.entity.user.models

data class UserDto(
    val username: String,
    val password: String,
)

data class User(
    val username: String,
    val password: String,
)

data class JwtRequest(
    val username: String,
    val password: String,
)

data class JwtResponse(
    val jwtToken: String,
)