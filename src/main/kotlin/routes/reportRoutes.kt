package routes

import conrollers.GetReportByIdController
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reportRoutes(repository: EmployeeTaskRegRepository, fileRepository: FileRepository,reportByIdController: GetReportByIdController){
    //Получение конкретного отчета
    get("/getReport/{reportId}"){ reportByIdController.handle(call)}

    get("/getReport/{reportId}/download"){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val reportId = call.parameters["reportId"]?.toInt()
        if (reportId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        if (login!=null){
            try {
                val path = repository.getReportFilePath(reportId)
                if (path == null) {
                    call.respond(HttpStatusCode.InternalServerError, "File path not found for report $reportId")
                    return@get
                }
                val byteArray = fileRepository.downloadFile(path)
                if (byteArray == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to download file for report $reportId")
                    return@get
                }
                call.respond(HttpStatusCode.OK,byteArray)
            }catch (ex:Exception){
                call.respond(HttpStatusCode.NotFound,"Report not found")
            }
        }
        else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }

    }

    patch("/markReport/{reportId}/{status}"){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val reportId = call.parameters["reportId"]?.toInt()
        val status = call.parameters["status"]?.toBooleanStrictOrNull()
        if (reportId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@patch
        }
        if (status == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid status")
            return@patch
        }
        if (login!=null) {
            val user = repository.getUserByLogin(login)
            if (user != null) {
                when (user.role) {
                    "employee" -> {
                        call.respond(HttpStatusCode.Forbidden, "Only director can mark report")
                    }

                    "director" -> {
                        try {
                            call.respond(HttpStatusCode.OK, repository.markReport(status, reportId))
                        } catch (ex: Exception) {
                            call.respond(HttpStatusCode.NotFound, "Report not found")
                        }
                    }
                }
            }
        }
        else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }



    }
}