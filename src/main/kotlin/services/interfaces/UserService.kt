package services.interfaces

import data.dto.RegistrationRequest

interface UserService {

    suspend fun register(request: RegistrationRequest):Result<String>

    suspend fun login():Result<String>
}