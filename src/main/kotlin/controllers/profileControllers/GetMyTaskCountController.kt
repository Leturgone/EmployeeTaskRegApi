package controllers.profileControllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import services.DirectorNotFoundException
import services.EmployeeNotFoundException
import services.InvalidRoleException
import services.UserNotFoundException
import services.interfaces.ProfileService

class GetMyTaskCountController(private val profileService: ProfileService){
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        if (login != null) {
            profileService.getMyTasksCount(login).onSuccess { count ->
                call.respond(HttpStatusCode.OK, count)
            }.onFailure { e ->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.InternalServerError, "User not found")
                    is EmployeeNotFoundException -> call.respond(HttpStatusCode.BadRequest,"Employee not found")
                    is DirectorNotFoundException -> call.respond(HttpStatusCode.BadRequest,"Director not found")
                    is InvalidRoleException -> call.respond(HttpStatusCode.Forbidden,"Invalid role")
                }
            }
        }
        else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}