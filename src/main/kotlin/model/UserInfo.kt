package model
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val sub: String,    // Subject (user ID) - required by IODC
    val name: String,
    val email: String,
    val username: String
)