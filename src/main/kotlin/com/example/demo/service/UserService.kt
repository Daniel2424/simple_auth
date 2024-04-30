package com.example.demo.service

import com.example.demo.models.User
import com.example.demo.models.UserDto
import com.example.demo.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    companion object {
        val map = mutableMapOf<String, User>()
    }

    fun registerNewUserAccount(userDto: UserDto): User {
        userRepository.findByUsername(userDto.username)?.let {
            throw RuntimeException("There is an account with that login: ${userDto.username}")
        }
        val user = User(
            username = userDto.username,
            password = passwordEncoder.encode(userDto.password)
        )
        return userRepository.saveUser(user)
    }

    fun findUserByUsername(username: String): User =
        userRepository.findByUsername(username) ?: throw RuntimeException()
}