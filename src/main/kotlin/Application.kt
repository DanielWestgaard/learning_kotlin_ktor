import database.configureDatabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging
import plugins.*

fun main() {
    val logger = KotlinLogging.logger {}
    logger.info { "Started Ktor API Application!" }

    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureDatabase()     // Configure database connection
    configureSerialization()  // Install JSON handling first
    configureSecurity()       // Install authentication
    configureCORS()          // Install CORS
    configureRouting()       // Install routes last (they depend on auth)
}