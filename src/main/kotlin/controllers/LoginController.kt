package controllers

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

class LoginController(private val userService: UserService) {
    suspend fun handle(call:ApplicationCall){
        try {
            val request = call.receive<LoginRequest>()
            userService.login(request).onSuccess { token ->
                call.respond(HttpStatusCode.OK,token)
            }.onFailure { e->
                when(e){
                    is InvalidPasswordException -> call.respond(HttpStatusCode.BadRequest,"Invalid password")
                    is InvalidEmailException -> call.respond(HttpStatusCode.BadRequest,"Invalid email")
                    is UserNotFoundException -> call.respond(HttpStatusCode.Unauthorized, "This user User not registered")
                    is WrongPasswordException -> call.respond(HttpStatusCode.Unauthorized, "Wrong password")
                }
            }
        }catch (e: BadRequestException){
            call.respond(HttpStatusCode.BadRequest,"Missing parameters")
        }

    }
}