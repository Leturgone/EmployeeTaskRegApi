package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import services.AuthException
import services.InvalidRoleException
import services.UserNotFoundException
import services.interfaces.EmployeeService

class GetEmpByNameController(private val employeeService: EmployeeService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val empName = call.parameters["empName"].toString()
        if (login != null) {
            employeeService.getEmployeeByName(login,empName).onSuccess { list ->
                call.respond(HttpStatusCode.OK,list)
            }.onFailure { e ->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.InternalServerError, "User not found")
                    is AuthException -> call.respond(HttpStatusCode.Forbidden,"Only directors can search employees")
                    is InvalidRoleException -> call.respond(HttpStatusCode.Forbidden,"Invalid role")
                    is Exception -> call.respond(HttpStatusCode.NotFound,"Director not found")
                }

            }
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}