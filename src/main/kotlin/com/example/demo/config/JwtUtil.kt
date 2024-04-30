package com.example.demo.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.TextCodec
import io.jsonwebtoken.impl.TextCodec.BASE64
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {
    private val secretKey = Base64.getEncoder()
        .encodeToString("your-256-bit-secret".toByteArray()) // Используйте надёжный ключ в продакшене

    fun extractUsername(token: String): String? =
        extractClaim(token) { it.subject }

    fun extractExpiration(token: String): Date =
        extractClaim(token) { it.expiration }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

    fun generateToken(username: String): String {
        val claims = mutableMapOf<String, Any>()
        return createToken(claims, username)
    }

    private fun createToken(claims: MutableMap<String, Any>, subject: String): String =
        Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
            .signWith(SignatureAlgorithm.HS256, secretKey).compact()

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    private fun isTokenExpired(token: String) = extractExpiration(token).before(Date())
}