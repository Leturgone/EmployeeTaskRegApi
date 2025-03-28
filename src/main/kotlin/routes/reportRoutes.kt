package routes

import conrollers.DownLoadReportController
import conrollers.GetReportByIdController
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reportRoutes(repository: EmployeeTaskRegRepository, fileRepository: FileRepository,
                       reportByIdController: GetReportByIdController,
                       downloadReportController: DownLoadReportController
                       ){
    //Получение конкретного отчета
    get("/getReport/{reportId}"){ reportByIdController.handle(call)}

    //Скачивание отчета
    get("/getReport/{reportId}/download"){downloadReportController.handle(call)}

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