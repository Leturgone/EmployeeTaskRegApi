package routes

import io.ktor.server.routing.*
import routes.params.TaskRoutesParams

fun Route.taskRoutes(taskRoutesParams: TaskRoutesParams){

    //Получение конкретного задания
    get("/getTask/{taskId}"){ taskRoutesParams.getTaskByIdController.handle(call) }

    //Скачивание файла задания
    get("/getTask/{taskId}/download"){ taskRoutesParams.downloadTaskController.handle(call) }

    //Удаление задания
    delete("/deleteTask/{taskId}"){taskRoutesParams.deleteTaskController.handle(call)}
}