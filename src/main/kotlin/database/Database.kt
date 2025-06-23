package database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

// Database table definition using Exposed - FIXED VERSION
object UsersTable : Table("users") {  // Pass table name as string parameter
    val id = varchar("id", 50)
    val username = varchar("username", 100).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 255)
    val password = varchar("password", 255)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}

object Database {
    fun connect(jdbcUrl: String, username: String, password: String) {
        // Configure HikariCP connection pool
        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            this.driverClassName = "org.postgresql.Driver"

            // Connection pool settings
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 300000 // 5 minutes
            connectionTimeout = 20000 // 20 seconds
            maxLifetime = 1200000 // 20 minutes

            // Useful for debugging connection issues
            leakDetectionThreshold = 60000 // 1 minute
        }

        val dataSource = HikariDataSource(config)

        // Connect Exposed to the database
        org.jetbrains.exposed.sql.Database.connect(dataSource)

        // Create tables if they don't exist (useful for development)
        transaction {
            SchemaUtils.create(UsersTable)
        }

        println("✅ Database connected successfully to $jdbcUrl")
    }
}