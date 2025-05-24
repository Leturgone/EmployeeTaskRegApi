package routes

import io.ktor.server.routing.*
import routes.params.ReportRoutesParams

fun Route.reportRoutes(reportRoutesParams: ReportRoutesParams){

    //Получение конкретного отчета
    get("/getReport/{reportId}"){ reportRoutesParams.reportByIdController.handle(call)}

    //Скачивание отчета
    get("/getReport/{reportId}/download"){reportRoutesParams.downloadReportController.handle(call)}

    //Отметка отчета статусом
    patch("/markReport/{reportId}/{status}"){reportRoutesParams.markReportController.handle(call)}

    //Обновление файла отчета
    patch("/updateReport/{reportId}"){reportRoutesParams.updateReportController.handle(call)}
}