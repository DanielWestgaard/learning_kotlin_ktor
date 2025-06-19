package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.LoginRequest
import model.TokenResponse
import service.JwtService
import service.UserService

fun Route.authRoutes() {
    val userService = UserService()
    val jwtService = JwtService()

    route("/auth") {

        // POST /auth/login - User login endpoint
        post("/login") {
            try {
                println("Login attempt received")

                // Parse the JSON request body
                val loginRequest = call.receive<LoginRequest>()
                println("Parsed login request: username=${loginRequest.username}")

                // Validate the user credentials
                val user = userService.validateUser(
                    username = loginRequest.username,
                    password = loginRequest.password
                )

                if (user != null) {
                    println("User validated successfully: ${user.username}")
                    // Generate tokens
                    val accessToken = jwtService.generateAccessToken(user)
                    val idToken = jwtService.generateIdToken(user)

                    // Send success response
                    call.respond(
                        HttpStatusCode.OK,
                        TokenResponse(
                            access_token = accessToken,
                            id_token = idToken
                        )
                    )
                } else {
                    println("Invalid credentials for username: ${loginRequest.username}")
                    // Invalid credentials
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("error" to "invalid_credentials", "message" to "Invalid username or password")
                    )
                }

            } catch (e: Exception) {
                // Handle any errors (like malformed JSON)
                println("Error in login: ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "bad_request", "message" to "Invalid request format: ${e.message}")
                )
            }
        }

        // GET /auth/login - Simple login form (for testing)
        get("/login") {
            call.respondText("""
                <!DOCTYPE html>
                <html>
                <head><title>Mini-ID Login</title></head>
                <body>
                    <h2>Mini-ID Service - Test Login</h2>
                    <p>Use these test accounts:</p>
                    <ul>
                        <li><strong>testuser</strong> / password123</li>
                        <li><strong>alice</strong> / alice123</li>
                    </ul>
                    
                    <h3>Try with curl:</h3>
                    <pre>
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
                    </pre>
                </body>
                </html>
            """.trimIndent(), ContentType.Text.Html)
        }
    }
}