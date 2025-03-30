package services.interfaces

import data.dto.LoginRequest
import data.dto.RegistrationRequest
import data.dto.TokenResponse

interface UserService {

    suspend fun register(request: RegistrationRequest): Result<TokenResponse>

    suspend fun login(request: LoginRequest): Result<TokenResponse>
}