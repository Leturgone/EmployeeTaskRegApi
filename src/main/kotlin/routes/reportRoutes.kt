package routes

import controllers.DownloadReportController
import controllers.GetReportByIdController
import controllers.MarkReportController
import io.ktor.server.routing.*

fun Route.reportRoutes(
    reportByIdController: GetReportByIdController,
    downloadReportController: DownloadReportController,
    markReportController: MarkReportController
                       ){
    //Получение конкретного отчета
    get("/getReport/{reportId}"){ reportByIdController.handle(call)}

    //Скачивание отчета
    get("/getReport/{reportId}/download"){downloadReportController.handle(call)}

    //Отметка отчета статусом
    patch("/markReport/{reportId}/{status}"){markReportController.handle(call)}
}