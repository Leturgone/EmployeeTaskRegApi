package routes

import io.ktor.server.routing.*
import routes.params.ReportRoutesParams

fun Route.reportRoutes(reportRoutesParams: ReportRoutesParams){

    //Получение конкретного отчета
    get("/getReport/{reportId}"){ reportRoutesParams.reportByIdController.handle(call)}

    //Получение отчета по id задания
    get("/getReportByTaskId/{taskId}"){ reportRoutesParams.reportByTaskIdController.handle(call)}


    //Скачивание отчета
    get("/getReport/{reportId}/download"){reportRoutesParams.downloadReportController.handle(call)}


    //Отметка отчета статусом
    patch("/markReport/{reportId}/{status}"){reportRoutesParams.markReportController.handle(call)}

    //Обновление файла отчета
    patch("/updateReport/{reportId}"){reportRoutesParams.updateReportController.handle(call)}

    //Удаление отчета
    delete("/deleteReport/{reportId}"){reportRoutesParams.deleteReportController.handle(call)}
}