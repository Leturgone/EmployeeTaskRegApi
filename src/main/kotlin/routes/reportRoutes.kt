package routes

import conrollers.DownLoadReportController
import conrollers.GetReportByIdController
import conrollers.MarkReportController
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reportRoutes(repository: EmployeeTaskRegRepository, fileRepository: FileRepository,
                       reportByIdController: GetReportByIdController,
                       downloadReportController: DownLoadReportController,
                       markReportController: MarkReportController
                       ){
    //Получение конкретного отчета
    get("/getReport/{reportId}"){ reportByIdController.handle(call)}

    //Скачивание отчета
    get("/getReport/{reportId}/download"){downloadReportController.handle(call)}

    patch("/markReport/{reportId}/{status}"){markReportController.handle(call)}
}