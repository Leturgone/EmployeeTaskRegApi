package model.Requests
import kotlinx.serialization.Serializable
@Serializable
data class RegistrationRequest(
    val login: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String
)