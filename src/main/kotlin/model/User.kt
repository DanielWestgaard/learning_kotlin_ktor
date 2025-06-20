package model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val name: String,
    val password: String  // Should be hashed?
)