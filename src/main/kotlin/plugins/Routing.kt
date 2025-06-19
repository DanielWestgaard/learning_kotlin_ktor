package plugins

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import routes.*

fun Application.configureRouting() {
    routing {
        // Health check endpoint
        get("/") {
            call.respondText("Mini-ID Service is running!")
        }

        // Authentication routes (/login, /token)
        authRoutes()

        // User info route (/userinfo)
        userInfoRoutes()
    }
}