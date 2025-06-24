package routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.LoginRequest
import model.RegisterRequest
import model.TokenResponse
import service.JwtService
import service.UserService

fun Route.authRoutes() {
    val userService = UserService()
    val jwtService = JwtService()

    route("/auth") {

        // POST /register - Create new user
        post("/register") {
            try {
                println("Registration attempt received.")

                // Parse the JSON request body
                val registerRequest = call.receive<RegisterRequest>()
                println("Parsed registration request: username=${registerRequest.username}, email=${registerRequest.email}")

                when (val result = userService.createUser(
                    username = registerRequest.username,
                    password = registerRequest.password,
                    name = registerRequest.name,
                    email = registerRequest.email
                )) {
                    is UserService.CreateUserResult.Success -> {
                        val createdUser = userService.findUserByUsername(registerRequest.username)
                        call.respond(
                            HttpStatusCode.Created,
                            "Successfully added user in DB!" +
                                    "\nUsername in db: '${createdUser?.username}'" +
                                    "\nEmail in db: '${createdUser?.email}'"
                        )
                        println("User created successfully created!")
                    }
                    is UserService.CreateUserResult.ValidationError -> {
                        println("Validation error: ${result.message}")
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "validation_error", "message" to result.message)
                        )
                    }
                    is UserService.CreateUserResult.UserExists -> {
                        println("User exists error: ${result.message}")
                        call.respond(
                            HttpStatusCode.Conflict,
                            mapOf("error" to "user_exists", "message" to result.message)
                        )
                    }
                    is UserService.CreateUserResult.DatabaseError -> {
                        println("Database error: ${result.message}")
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to "database_error", "message" to "Failed to create user")
                        )
                    }
                }

            } catch (e: Exception) {
                println("Error in registration: ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "bad_request", "message" to "Invalid request format: ${e.message}")
                )
            }
        }

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
            // Using these two initial ones as examples
            val testUserOne = userService.findUserById("1")
            val testUserTwo = userService.findUserById("2")

            call.respondText("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Mini-ID Login & Register</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; }
                        .section { margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; }
                        .code { background: #f5f5f5; padding: 10px; border-radius: 5px; font-family: monospace; white-space: pre; }
                    </style>
                </head>
                <body>
                    <h1>Mini-ID Service - Test Login & Registration</h1>
                    
                    <div class="section">
                        <h2>🔐 Test Login</h2>
                        <p>Use these test accounts:</p>
                        <ul>
                            <li><strong>${testUserOne?.username}</strong> / ${testUserOne?.password}</li>
                            <li><strong>${testUserTwo?.username}</strong> / ${testUserTwo?.password}</li>
                        </ul>
                        
                        <h3>Login with curl:</h3>
                        <div class="code">curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "${testUserOne?.username}", "password": "${testUserOne?.password}"}'</div>
                    </div>

                    <div class="section">
                        <h2>✨ Test Registration</h2>
                        <p>Create a new user account:</p>
                        
                        <h3>Register with curl:</h3>
                        <div class="code">curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "frodo_baggins",
    "email": "frodo@shire.com", 
    "name": "Frodo Baggins",
    "password": "MyPrecious123"
  }'</div>
                    </div>

                    <div class="section">
                        <h2>🔍 Test User Info</h2>
                        <p>After login, use the access_token to get user info:</p>
                        <div class="code">curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  http://localhost:8080/userinfo</div>
                    </div>
                </body>
                </html>
            """.trimIndent(), ContentType.Text.Html)
        }
    }
}