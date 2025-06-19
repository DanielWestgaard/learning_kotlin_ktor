import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import plugins.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()  // Install JSON handling first
    configureSecurity()       // Install authentication
    configureCORS()          // Install CORS
    configureRouting()       // Install routes last (they depend on auth)
}