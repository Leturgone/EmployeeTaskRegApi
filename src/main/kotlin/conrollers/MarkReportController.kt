package conrollers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import servicies.AuthException
import servicies.InvalidLoginException
import servicies.ReportService

class MarkReportController(private val reportService: ReportService) {
    suspend fun handle(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val reportId = call.parameters["reportId"]?.toInt()
        val status = call.parameters["status"]?.toBooleanStrictOrNull()
        if (reportId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid report id")
            return
        }
        if (status == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid status")
            return
        }
        if (login!=null) {
            reportService.markReport(login, reportId, status).onSuccess {
                call.respond(HttpStatusCode.OK)
            }.onFailure { e->
                when(e){
                    is AuthException -> call.respond(HttpStatusCode.Forbidden, "Only director can mark report")
                    is InvalidLoginException -> call.respond(HttpStatusCode.InternalServerError, "User not found")
                    is Exception -> call.respond(HttpStatusCode.NotFound, "Report not found")
                }
            }
        }
        else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }
    }
}