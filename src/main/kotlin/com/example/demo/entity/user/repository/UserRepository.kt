package com.example.demo.entity.user.repository

import com.example.demo.entity.user.models.User
import org.springframework.stereotype.Repository

@Repository
class UserRepository {
    companion object {
        private val map = mutableMapOf<String, User>()
    }

    fun findByUsername(userName: String): User? {
        return map[userName]
    }

    fun saveUser(user: User): User {
        map[user.username] = user
        return user
    }
}