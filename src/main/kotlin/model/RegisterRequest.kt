package model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    // This needs to be the same as User(), just without ID
    val username: String,
    val email: String,
    val name: String,
    val password: String // TODO: Should be hashed
)