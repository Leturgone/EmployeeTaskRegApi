package services.implementations

import autharization.CheckMailPasswordUtils
import autharization.Tokens
import data.dto.RegistrationRequest
import domain.repository.EmployeeTaskRegRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import services.AlreadyRegisterException
import services.InvalidEmailException
import services.InvalidPasswordException
import services.interfaces.UserService

class UserServiceImpl(private val empRepository:EmployeeTaskRegRepository): UserService {
    override suspend fun register(request:RegistrationRequest): Result<String> {
        if (!CheckMailPasswordUtils.validateEmail(request.login)){
            return Result.failure(InvalidEmailException())
        }
        if (!CheckMailPasswordUtils.validatePassword(request.password)){
            return Result.failure(InvalidPasswordException())
        }
        val hashedPassword = CheckMailPasswordUtils.hashPassword(request.password)
        return try {
            empRepository.addUser(request.login,hashedPassword,request.name,request.dirName)
            val token = Tokens.generateToken(request.login,request.password)
            Result.success(token)
        }catch (ex: ExposedSQLException){
            Result.failure(AlreadyRegisterException())
        }
        catch (ex: IllegalStateException) {
            Result.failure(ex)
        }
    }

    override suspend fun login(): Result<String> {
        TODO("Not yet implemented")
    }


}