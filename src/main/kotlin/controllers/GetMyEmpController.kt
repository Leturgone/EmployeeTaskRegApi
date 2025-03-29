package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import servicies.AuthException
import servicies.InvalidRoleException
import servicies.ProfileService
import servicies.UserNotFoundException

class GetMyEmpController(private val profileService: ProfileService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        if (login != null) {
            profileService.getMyEmployees(login).onSuccess { list ->
                call.respond(HttpStatusCode.OK,list)
            }.onFailure { e ->
                when(e){
                    is AuthException -> call.respond(HttpStatusCode.Forbidden,"Only director have employees")
                    is UserNotFoundException -> call.respond(HttpStatusCode.InternalServerError, "User not found")
                    is NoSuchElementException -> call.respond(HttpStatusCode.NotFound,"Director not found")
                    is InvalidRoleException -> call.respond(HttpStatusCode.Forbidden,"Invalid role")
                }
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }
    }
}