package routes.params

import controllers.taskControllers.DeleteTaskController
import controllers.taskControllers.DownloadTaskController
import controllers.taskControllers.GetTaskByIdController

data class TaskRoutesParams(
    val getTaskByIdController: GetTaskByIdController,
    val downloadTaskController: DownloadTaskController,
    val deleteTaskController: DeleteTaskController
)
