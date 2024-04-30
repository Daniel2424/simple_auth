package com.example.demo.entity.user.controller

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
class PostResolver {

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
}

data class Post(val id: UUID, val title: String)