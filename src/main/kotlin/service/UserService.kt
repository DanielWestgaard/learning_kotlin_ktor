package service

import model.User
import database.UsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {

    fun validateUser(username: String, password: String): User? {
        return transaction {
            // Query the database for a user with matching username and password
            UsersTable
                .select { (UsersTable.username eq username) and (UsersTable.password eq password) }
                .singleOrNull()
                ?.let { row ->
                    User(
                        id = row[UsersTable.id],
                        username = row[UsersTable.username],
                        email = row[UsersTable.email],
                        name = row[UsersTable.name],
                        password = row[UsersTable.password]
                    )
                }
        }
    }

    fun findUserById(id: String): User? {
        return transaction {
            UsersTable
                .select { UsersTable.id eq id }
                .singleOrNull()
                ?.let { row ->
                    User(
                        id = row[UsersTable.id],
                        username = row[UsersTable.username],
                        email = row[UsersTable.email],
                        name = row[UsersTable.name],
                        password = row[UsersTable.password]
                    )
                }
        }
    }

    fun findUserByUsername(username: String): User? {
        return transaction {
            UsersTable
                .select { UsersTable.username eq username }
                .singleOrNull()
                ?.let { row ->
                    User(
                        id = row[UsersTable.id],
                        username = row[UsersTable.username],
                        email = row[UsersTable.email],
                        name = row[UsersTable.name],
                        password = row[UsersTable.password]
                    )
                }
        }
    }

    // Bonus: Method to create a new user
    fun createUser(username: String, email: String, name: String, password: String): User? {
        return transaction {
            try {
                val userId = generateUserId() // Simple ID generation

                UsersTable.insert {
                    it[id] = userId
                    it[UsersTable.username] = username
                    it[UsersTable.email] = email
                    it[UsersTable.name] = name
                    it[UsersTable.password] = password // In production: hash this!
                }

                User(userId, username, email, name, password)
            } catch (e: Exception) {
                println("Failed to create user: ${e.message}")
                null
            }
        }
    }

    private fun generateUserId(): String {
        // Simple ID generation - in production use UUID.randomUUID().toString()
        return System.currentTimeMillis().toString()
    }
}