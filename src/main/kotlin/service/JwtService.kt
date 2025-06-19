package service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import model.User
import java.util.Date

class JwtService {
    private val secret = "your-256-bit-secret-key-here-change-this-in-production"
    private val issuer = "mini-id-service"
    private val audience = "mini-id-users"
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateAccessToken(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", user.username)
            .withClaim("user_id", user.id)
            .withClaim("scope", "read_write")  // OAuth2 scopes
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000))  // 1 Hour (in seconds)
            .sign(algorithm)
    }

    fun generateIdToken(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withSubject(user.id) // 'sub' claim - user identifier
            .withClaim("name", user.name)
            .withClaim("email", user.email)
            .withClaim("username", user.username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000)) // 1 hour
            .sign(algorithm)
    }
}