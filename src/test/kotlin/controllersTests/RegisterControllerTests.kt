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

class RegisterControllerTests(private val userService: UserService) {
    suspend fun handle(call:ApplicationCall){
        try {
            val request = call.receive<RegistrationRequest>()
            userService.register(request).onSuccess { token ->
                call.respond(HttpStatusCode.OK,token)
            }.onFailure { e ->
                when(e){
                    is InvalidPasswordException -> call.respond(HttpStatusCode.BadRequest,"Invalid password")
                    is InvalidEmailException -> call.respond(HttpStatusCode.BadRequest,"Invalid email")
                    is DirectorNotFoundException -> call.respond(HttpStatusCode.BadRequest,"Director not found")
                    is AlreadyRegisterException -> call.respond(HttpStatusCode.Conflict,"This user already have account")
                    is IllegalStateException -> call.respond(HttpStatusCode.BadRequest)
                }
            }
        }catch (e: BadRequestException){
            call.respond(HttpStatusCode.BadRequest,"Missing parameters")
        }

    }
}