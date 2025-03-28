package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import servicies.InvalidRoleException
import servicies.ProfileService
import servicies.UserNotFoundException

class GetProfileController(private val profileService: ProfileService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()

        if (login != null) {
            profileService.getProfile(login).onSuccess { worker ->
                call.respond(HttpStatusCode.OK,worker)
            }.onFailure { e->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.NotFound, "User not found")
                    is InvalidRoleException -> call.respond(HttpStatusCode.InternalServerError,"Invalid role")
                }

            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}