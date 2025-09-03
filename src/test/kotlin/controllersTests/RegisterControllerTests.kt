package controllersTests

import data.dto.RegistrationRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import services.AlreadyRegisterException
import services.DirectorNotFoundException
import services.InvalidEmailException
import services.InvalidPasswordException
import services.interfaces.UserService

class RegisterControllerTests() {
}