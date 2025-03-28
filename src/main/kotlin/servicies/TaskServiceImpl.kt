package servicies

import data.model.Task
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository

class TaskServiceImpl(private val empRepository: EmployeeTaskRegRepository,
                      private val fileRepository: FileRepository
):TaskService {
    override suspend fun getTaskById(taskId: Int): Result<Task> {
        return try {
            Result.success(empRepository.getTask(taskId))
        }catch (ex:Exception){
            Result.failure(ex)
        }
    }

    override suspend fun downloadTask(taskId: Int): Result<ByteArray> {
        TODO("Not yet implemented")
    }
}