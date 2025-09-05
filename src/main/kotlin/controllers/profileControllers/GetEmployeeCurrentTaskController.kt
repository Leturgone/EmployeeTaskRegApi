package controllers.profileControllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import services.interfaces.EmployeeService

class GetEmployeeCurrentTaskController(private val employeeService: EmployeeService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val employeeId = call.parameters["employeeId"]?.toIntOrNull()
        if (employeeId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid employee id")
            return
        }
        if (login!=null){
            employeeService.getEmployeeCurrentTask(employeeId).onSuccess { employeeCurrentTask ->
                call.respond(HttpStatusCode.OK,employeeCurrentTask)
            }.onFailure {
                call.respond(HttpStatusCode.NotFound,"Employee current task not found")
            }
        }
        else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}