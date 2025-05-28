package routes.params

import controllers.DeleteTaskController
import controllers.DownloadTaskController
import controllers.GetTaskByIdController

data class TaskRoutesParams(
    val getTaskByIdController: GetTaskByIdController,
    val downloadTaskController: DownloadTaskController,
    val deleteTaskController: DeleteTaskController
)
