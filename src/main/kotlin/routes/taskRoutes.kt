package routes

import controllers.DownloadTaskController
import controllers.GetTaskByIdController
import io.ktor.server.routing.*
import routes.params.TaskRoutesParams

fun Route.taskRoutes(taskRoutesParams: TaskRoutesParams){

    //Получение конкретного задания
    get("/getTask/{taskId}"){ taskRoutesParams.getTaskByIdController.handle(call) }

    //Скачивание файла задания
    get("/getTask/{taskId}/download"){ taskRoutesParams.downloadTaskController.handle(call) }
}