package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import servicies.DownloadFileException
import servicies.FilePathException
import servicies.ReportService

class DownloadReportController(private val reportService: ReportService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val reportId = call.parameters["reportId"]?.toInt()
        if (reportId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }
        if (login!=null){
            reportService.downloadReport(reportId).onSuccess { byteArray ->
                call.respond(HttpStatusCode.OK,byteArray)
            }.onFailure { e->
                when(e){
                    is FilePathException -> call.respond(HttpStatusCode.InternalServerError,"File path not found for report $reportId")
                    is DownloadFileException -> call.respond(HttpStatusCode.InternalServerError,"Failed to download file for report $reportId")
                    is Exception -> call.respond(HttpStatusCode.NotFound, "Report not found")
                }

            }
        }
        else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }
    }

}