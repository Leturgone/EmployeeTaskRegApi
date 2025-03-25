package routes

import data.repository.EmployeeTaskRegRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getEmployeeRoute(repository: EmployeeTaskRegRepository){
    get("/employee/{employeeId}"){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val employeeId = call.parameters["employeeId"]?.toInt()
        if (employeeId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        if (login!=null){
            try {
                call.respond(HttpStatusCode.OK,repository.getEmployeeById(employeeId))
            }catch (ex:Exception){
                call.respond(HttpStatusCode.NotFound,"Employee not found")
            }
        }
        else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }
    }
}