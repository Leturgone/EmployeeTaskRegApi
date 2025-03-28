package routes

import controllers.DownloadTaskController
import controllers.GetTaskByIdController
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(
                     getTaskByIdController: GetTaskByIdController,
                     downloadTaskController: DownloadTaskController){

    //Получение конкретного задания
    get("/getTask/{taskId}"){ getTaskByIdController.handle(call) }

    //Скачивание файла задания
    get("/getTask/{taskId}/download"){ downloadTaskController.handle(call) }
}