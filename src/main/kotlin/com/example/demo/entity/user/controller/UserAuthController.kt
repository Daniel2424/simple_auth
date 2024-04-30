package com.example.demo.entity.user.controller

import com.example.demo.config.*
import com.example.demo.entity.user.models.JwtRequest
import com.example.demo.entity.user.models.JwtResponse
import com.example.demo.entity.user.models.UserDto
import com.example.demo.entity.user.service.UserService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.stereotype.Component


@Component
@GraphQLApi
class UserGraphQLService(
    private val userService: UserService,
    private val jwtTokenUtil: JwtUtil,
    //private val authenticationManager: AuthenticationManager
) {
    @GraphQLMutation(name = "registerUser")
    fun registerUser(@GraphQLArgument(name = "user") userDto: UserDto): String {
        userService.registerNewUserAccount(userDto)
        return "User registered successfully"
    }

    @GraphQLMutation(name = "login")
    fun login(@GraphQLArgument(name = "credentials") jwtRequest: JwtRequest): JwtResponse {
        //authenticate(jwtRequest.username, jwtRequest.password)
        val userDetails = userService.findUserByUsername(jwtRequest.username)
        val token = jwtTokenUtil.generateToken(userDetails.username)
        return JwtResponse(token)
    }

    private fun authenticate(username: String, password: String) {
        try {
            //authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw Exception("USER_DISABLED", e)
        } catch (e: BadCredentialsException) {
            throw Exception("INVALID_CREDENTIALS", e)
        }
    }
}
