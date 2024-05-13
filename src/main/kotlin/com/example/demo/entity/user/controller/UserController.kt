package com.example.demo.entity.user.controller

import com.example.demo.entity.auth.AuthService
import com.example.demo.entity.user.models.Post
import com.example.demo.entity.user.models.User
import com.example.demo.entity.user.service.UserService
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component
import java.util.*

@Component
@GraphQLApi
class TestController(
    private val userService: UserService,
    private val authService: AuthService,
) {
    @GraphQLQuery(name = "welcome")
    fun welcomePage(): List<Post> {
        val user = authService.getAuthUser()
        // какая-либо логика
        return listOf(
            Post(
                id = UUID.randomUUID(),
                title = "some title" + user?.username,
            )
        )
    }

    @GraphQLQuery(name = "getUserInfo")
    fun getUserInfo(): User {
        return authService.getAuthUser()
    }
}

