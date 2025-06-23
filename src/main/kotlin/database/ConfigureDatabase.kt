package database

import io.ktor.server.application.Application
import database.Database

fun Application.configureDatabase() {
    // Database configuration
    // In a real production app, these would come from environment variables or config files
    val jdbcUrl = "jdbc:postgresql://localhost:5432/kotlin_learning"
    val username = "dev_user"
    val password = "dev_password"

    try {
        Database.connect(jdbcUrl, username, password)
        println("🚀 Database configuration completed")
    } catch (e: Exception) {
        println("❌ Failed to connect to database: ${e.message}")
        println("💡 Make sure Docker is running: docker-compose up -d")
        throw e
    }
}