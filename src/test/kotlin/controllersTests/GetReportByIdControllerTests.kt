package controllersTests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

import io.ktor.server.response.*
import services.interfaces.ReportService

class GetReportByIdControllerTests(private val reportService: ReportService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val reportId = call.parameters["reportId"]?.toIntOrNull()
        if (reportId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid report id")
            return
        }
        if (login!=null){
            reportService.getReportById(reportId).onSuccess { report ->
                call.respond(HttpStatusCode.OK, report)
            }.onFailure {
                call.respond(HttpStatusCode.NotFound,"Report not found")
            }
        }
        else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}