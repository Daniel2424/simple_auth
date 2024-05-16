package com.example.demo.entity.user.service

import com.example.demo.entity.user.models.UserDto
import com.example.demo.entity.user.models.User
import com.example.demo.entity.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun registerNewUserAccount(userDto: UserDto): User {
        userRepository.findByUsername(userDto.username)?.let {
            throw RuntimeException("There is an account with that login: ${userDto.username}")
        }
        val user = User(
            firstName = userDto.firstName,
            lastName = userDto.lastName,
            email = userDto.email,
            username = userDto.username,
            password = passwordEncoder.encode(userDto.password),
            role = userDto.role,
        )
        return userRepository.saveUser(user)
    }

    fun findUserByUsername(username: String): User =
        userRepository.findByUsername(username) ?: throw RuntimeException()

    fun findUserById(userId: Int): User? {
        return userRepository.findById(userId)
    }
}