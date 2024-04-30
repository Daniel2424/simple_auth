package com.example.demo.controller

import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
@GraphQLApi
class PostResolver(
    //private val userDetailService: InMemoryUserDetailsManager,
) {

    //@PreAuthorize("hasRole('VIEWER')")
    @GraphQLQuery(name = "getPosts")
    fun getPosts(): List<Post> {
        return listOf(
            Post(
                id = UUID.randomUUID(),
                title = "some title",
            )
        )
    }

    @GraphQLMutation(name = "registerNewUser")
    fun registerNewUser(username: String, password: String): Boolean {
        //val encodedPassword = passwordEncoder().encode(password)
        val user: UserDetails = User.withUsername(username)
            .password(password)
            .roles("VIEWER")
            .build()
        try {
            //userDetailService.createUser(user)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }
}

data class Post(val id: UUID, val title: String)