package routes.params

import controllers.DownloadTaskController
import controllers.GetTaskByIdController

data class TaskRoutesParams(
    val getTaskByIdController: GetTaskByIdController,
    val downloadTaskController: DownloadTaskController
)
