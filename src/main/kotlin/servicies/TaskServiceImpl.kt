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
        try {
            val path = empRepository.getTaskFilePath(taskId)
                ?: return Result.failure(FilePathException())
            val byteArray = fileRepository.downloadFile(path)
                ?: return Result.failure(DownloadFileException())
            return Result.success(byteArray)
        }catch (ex:Exception){
            return Result.failure(ex)
        }
    }
}