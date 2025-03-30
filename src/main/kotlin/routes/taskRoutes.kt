package routes

import controllers.DownloadTaskController
import controllers.GetTaskByIdController
import io.ktor.server.routing.*

fun Route.taskRoutes(
                     getTaskByIdController: GetTaskByIdController,
                     downloadTaskController: DownloadTaskController){

    //Получение конкретного задания
    get("/getTask/{taskId}"){ getTaskByIdController.handle(call) }

    //Скачивание файла задания
    get("/getTask/{taskId}/download"){ downloadTaskController.handle(call) }
}