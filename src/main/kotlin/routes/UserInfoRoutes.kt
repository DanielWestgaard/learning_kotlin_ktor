package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.UserInfo
import service.UserService

fun Route.userInfoRoutes() {
    val userService = UserService()

    // This route requires JWT authentication
    authenticate("auth-jwt") {
        get("/userinfo") {
            try {
                // Get the JWT principal (user info from the token)
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()

                // Find the user
                val user = userService.findUserByUsername(username)

                if (user != null) {
                    // Return user info (OIDC standard format)
                    call.respond(
                        HttpStatusCode.OK,
                        UserInfo(
                            sub = user.id,           // 'sub' is the user ID
                            name = user.name,
                            email = user.email,
                            username = user.username
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "user_not_found")
                    )
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "server_error", "message" to "Could not retrieve user info")
                )
            }
        }
    }
}