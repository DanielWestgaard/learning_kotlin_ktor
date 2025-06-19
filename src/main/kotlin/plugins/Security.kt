package plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    // Get JWT config from application.conf
//    val jwtSecret = environment.config.property("jwt.secret").getString()
//    val jwtIssuer = environment.config.property("jwt.issuer").getString()
//    val jwtAudience = environment.config.property("jwt.audience").getString()
//    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = "your-256-bit-secret-key-here-change-this-in-production"
    val jwtIssuer = "mini-id-service"
    val jwtAudience = "mini-id-users"
    val jwtRealm = "Mini ID Service"

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                // Check if the token has required claims
                if (credential.payload.getClaim("username").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}