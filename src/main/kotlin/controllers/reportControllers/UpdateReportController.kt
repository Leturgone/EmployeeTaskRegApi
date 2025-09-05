package controllers.reportControllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import services.AuthException
import services.InvalidTaskJsonException
import services.MissingFileException
import services.UserNotFoundException
import services.interfaces.ReportService

class UpdateReportController(private val reportService: ReportService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()

        val reportId = call.parameters["reportId"]?.toIntOrNull()

        val multiPartData = call.receiveMultipart()

        val login = principal?.payload?.getClaim("login")?.asString()

        if (reportId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid report id")
            return
        }
        if (login != null) {
            reportService.updateReport(reportId,multiPartData,login).onSuccess{
                call.respond(HttpStatusCode.OK)
            }.onFailure { e->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.NotFound, "User not found")
                    is AuthException -> call.respond(HttpStatusCode.Forbidden,"Only employee can update reports")
                    is InvalidTaskJsonException -> call.respond(HttpStatusCode.BadRequest, "Invalid Task JSON")
                    is NullPointerException -> call.respond(HttpStatusCode.InternalServerError,"Error deleting file")
                    is NoSuchElementException -> call.respond(HttpStatusCode.BadRequest,"No task for report found")
                    is ExposedSQLException -> call.respond(HttpStatusCode.Conflict,"Reports must be unique")
                    is MissingFileException -> call.respond(HttpStatusCode.BadRequest,"Missing report file")
                }
            }
        }else{
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}