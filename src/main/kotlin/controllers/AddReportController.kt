package controllers

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

class AddReportController(private val reportService: ReportService)  {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val multiPartData = call.receiveMultipart()

        val login = principal?.payload?.getClaim("login")?.asString()

        if (login != null) {
            reportService.addReport(multiPartData,login).onSuccess{
                call.respond(HttpStatusCode.OK)
            }.onFailure { e->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.NotFound, "User not found")
                    is AuthException -> call.respond(HttpStatusCode.Forbidden,"Only employee can create reports")
                    is InvalidTaskJsonException -> call.respond(HttpStatusCode.BadRequest, "Invalid Task JSON")
                    is NullPointerException -> call.respond(HttpStatusCode.InternalServerError,"Error saving file")
                    is NoSuchElementException -> call.respond(HttpStatusCode.BadRequest,"No task for report found")
                    is ExposedSQLException -> call.respond(HttpStatusCode.BadRequest,"Reports must be unique")
                    is MissingFileException -> call.respond(HttpStatusCode.BadRequest,"Missing report file")
                }
            }
        }else{
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}