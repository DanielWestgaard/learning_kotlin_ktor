package service

import model.User
import database.UsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import java.util.UUID

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

    // Enhanced user creation with validation
    fun createUser(username: String, email: String, name: String, password: String): CreateUserResult {
        // Validation
        val validationError = validateUserInput(username, email, name, password)
        if (validationError != null) {
            return CreateUserResult.ValidationError(validationError)
        }

        return transaction {
            try {
                // Check if username already exists
                val existingUser = UsersTable.select { UsersTable.username eq username }.singleOrNull()
                if (existingUser != null) {
                    return@transaction CreateUserResult.UserExists("Username already exists")
                }

                // Check if email already exists
                val existingEmail = UsersTable.select { UsersTable.email eq email }.singleOrNull()
                if (existingEmail != null) {
                    return@transaction CreateUserResult.UserExists("Email already exists")
                }

                val userId = UUID.randomUUID().toString()
                val hashedPassword = hashPassword(password) // Not ready to use this yet (for learning)

                UsersTable.insert {
                    it[id] = userId
                    it[UsersTable.username] = username
                    it[UsersTable.email] = email
                    it[UsersTable.name] = name
                    it[UsersTable.password] = password
                }

                val user = User(userId, username, email, name, password)
                CreateUserResult.Success(user)

            } catch (e: Exception) {
                println("Failed to create user: ${e.message}")
                CreateUserResult.DatabaseError("Failed to create user: ${e.message}")
            }
        }
    }

    // Input validation
    private fun validateUserInput(username: String, email: String, name: String, password: String): String? {
        if (username.isBlank() || username.length < 3) {
            return "Username must be at least 3 characters long"
        }

        if (username.length > 50) {
            return "Username must not exceed 50 characters"
        }

        if (!isValidEmail(email)) {
            return "Invalid email format"
        }

        if (name.isBlank() || name.length < 2) {
            return "Name must be at least 2 characters long"
        }

        if (password.length < 6) {
            return "Password must be at least 6 characters long"
        }

        return null
    }

    // Simple email validation
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length > 5
    }

    // Simple password hashing (for learning - use bcrypt in production!)
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    // Result sealed class for better error handling
    // Sealed classes "define a closed set of subclasses" (?), and compiler can guarantee that all possible subclasses
    // are covered in 'when' expressions (eliminating need for 'else' in many cases).
    sealed class CreateUserResult {
        data class Success(val user: User) : CreateUserResult()
        data class ValidationError(val message: String) : CreateUserResult()
        data class UserExists(val message: String) : CreateUserResult()
        data class DatabaseError(val message: String) : CreateUserResult()
    }
}