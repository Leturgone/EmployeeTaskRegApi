package controllersTests

import data.dto.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import services.InvalidEmailException
import services.InvalidPasswordException
import services.UserNotFoundException
import services.WrongPasswordException
import services.interfaces.UserService

class LoginControllerTests() {
}