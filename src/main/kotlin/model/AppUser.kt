package model
import kotlinx.serialization.Serializable

@Serializable
data class AppUser(
    val login: String,
    val passwordHash: String,
    val role: String
)