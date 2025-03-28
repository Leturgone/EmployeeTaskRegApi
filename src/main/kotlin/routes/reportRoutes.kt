package routes

import conrollers.DownLoadReportController
import conrollers.GetReportByIdController
import conrollers.MarkReportController
import io.ktor.server.routing.*

fun Route.reportRoutes(
                       reportByIdController: GetReportByIdController,
                       downloadReportController: DownLoadReportController,
                       markReportController: MarkReportController
                       ){
    //Получение конкретного отчета
    get("/getReport/{reportId}"){ reportByIdController.handle(call)}

    //Скачивание отчета
    get("/getReport/{reportId}/download"){downloadReportController.handle(call)}

    //Отметка отчета статусом
    patch("/markReport/{reportId}/{status}"){markReportController.handle(call)}
}