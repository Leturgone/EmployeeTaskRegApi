package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import servicies.*

class GetMyReportsController(private val profileService: ProfileService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        if (login != null) {
            profileService.getMyReports(login).onSuccess { reports ->
                call.respond(HttpStatusCode.OK,reports)
            }.onFailure { e ->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.InternalServerError, "User not found")
                    is EmployeeNotFoundException -> call.respond(HttpStatusCode.NotFound,"Employee not found")
                    is DirectorNotFoundException -> call.respond(HttpStatusCode.NotFound,"Director not found")
                    is InvalidRoleException -> call.respond(HttpStatusCode.Forbidden,"Invalid role")
                }
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }
    }
}