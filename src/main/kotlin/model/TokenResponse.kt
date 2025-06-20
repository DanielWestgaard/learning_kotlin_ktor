package model

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val access_token: String,
    val id_token: String,
    val token_type: String = "Bearer",
    val expires_in: Int = 3600  // 1 hour (in seconds)
)