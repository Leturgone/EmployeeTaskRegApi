package controllers.reportControllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import services.AuthException
import services.UserNotFoundException
import services.interfaces.ReportService

class DeleteReportController(private val reportService: ReportService) {
    suspend fun handle(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()

        val login = principal?.payload?.getClaim("login")?.asString()
        val reportId = call.parameters["reportId"]?.toIntOrNull()
        if (reportId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid report id")
            return
        }
        if (login != null) {
            reportService.deleteReport(reportId,login).onSuccess{
                call.respond(HttpStatusCode.OK)
            }.onFailure { e->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.InternalServerError, "User not found")
                    is AuthException -> call.respond(HttpStatusCode.Forbidden,"Only employee can delete reports")
                    is Exception -> call.respond(HttpStatusCode.BadRequest, "Report not found")
                }
            }
        }else{
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}