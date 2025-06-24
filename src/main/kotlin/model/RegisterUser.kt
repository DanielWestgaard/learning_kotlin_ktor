package model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser (
    // This needs to be the same as User(), just without ID
    val username: String,
    val email: String,
    val name: String,
    val password: String  // Should be hashed?
)