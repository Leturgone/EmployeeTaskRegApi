package data.model
import kotlinx.serialization.Serializable

@Serializable
data class AppUser(
    val id:Int,
    val login: String,
    val passwordHash: String,
    val role: String
)