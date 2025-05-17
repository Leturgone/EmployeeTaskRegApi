package services.implementations

import autharization.CheckMailPasswordUtils
import autharization.Tokens
import data.dto.LoginRequest
import data.dto.RegistrationRequest
import data.dto.TokenResponse
import domain.repository.EmployeeTaskRegRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import services.*
import services.interfaces.UserService

class UserServiceImpl(private val empRepository:EmployeeTaskRegRepository): UserService {
    override suspend fun register(request:RegistrationRequest): Result<TokenResponse> {
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
            Result.success(TokenResponse(token))
        }catch (ex: ExposedSQLException){
            Result.failure(AlreadyRegisterException())
        }
        catch (ex:NoSuchElementException){
            Result.failure(DirectorNotFoundException())
        }
        catch (ex: IllegalStateException) {
            Result.failure(ex)
        }
    }

    override suspend fun login(request: LoginRequest): Result<TokenResponse> {
        if (!CheckMailPasswordUtils.validateEmail(request.login)){
            return Result.failure(InvalidEmailException())
        }
        if (!CheckMailPasswordUtils.validatePassword(request.password)){
            return Result.failure(InvalidPasswordException())
        }

        val user = empRepository.getUserByLogin(request.login)?: return Result.failure(UserNotFoundException())

        if (!CheckMailPasswordUtils.verifyPassword(request.password,user.passwordHash)){
            return Result.failure(WrongPasswordException())
        }
        val token = Tokens.generateToken(user.login,user.role)
        return Result.success(TokenResponse(token))
    }


}