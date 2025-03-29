package servicies

import data.model.Task
import io.ktor.http.content.*

interface TaskService {
    suspend fun getTaskById(taskId:Int):Result<Task>

    suspend fun downloadTask(taskId: Int): Result<ByteArray>

    suspend fun addTask(multiPartData: MultiPartData, login:String):Result<Unit>
}