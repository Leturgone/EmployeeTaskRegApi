package servicies

import data.model.Task

interface TaskService {
    suspend fun getTaskById(taskId:Int):Result<Task>

    suspend fun downloadTask(taskId: Int): Result<ByteArray>
}