package services.implementations

import domain.model.Task
import domain.repository.EmployeeTaskRegRepository
import domain.repository.FileRepository
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.exceptions.ExposedSQLException
import services.*
import services.interfaces.TaskService

class TaskServiceImpl(private val empRepository: EmployeeTaskRegRepository,
                      private val fileRepository: FileRepository
): TaskService {
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

    override suspend fun addTask(multiPartData: MultiPartData,login:String): Result<Unit> {
        var task: Task? = null
        var fileBytes: ByteArray? = null
        var fileName = "unknownTaskFile.pdf"
        val user = empRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        if(user.role!="director"){ return Result.failure(AuthException()) }

        try {
            multiPartData.forEachPart { partData ->
                when(partData){
                    is PartData.FormItem ->{
                        if (partData.name =="taskJson"){
                            val jsonTask = partData.value
                            task = try{
                                Json.decodeFromString<Task>(jsonTask)
                            }catch (e:Exception){
                                partData.dispose
                                throw InvalidTaskJsonException()
                            }
                        }
                        partData.dispose
                    }
                    is PartData.FileItem ->{
                        partData.originalFileName?.let { fileName = it }
                        val channel = partData.provider()
                        fileBytes = channel.readRemaining().readByteArray()
                        partData.dispose
                    }
                    else -> {partData.dispose}
                }
            }
        }catch (e: InvalidTaskJsonException){
            return Result.failure(e)
        }
        if (task==null) {return Result.failure(Exception())}

        try {
            if (fileBytes!=null){
                val taskId = empRepository.addTask(task!!)
                val path = fileRepository.uploadFile(user.id,user.role,
                    fileName,fileBytes!!)
                empRepository.updateTaskPath(path!!,taskId)
                return Result.success(Unit)
            }else{
                empRepository.addTask(task!!)
                return Result.success(Unit)
            }
        }catch (ex:NullPointerException){
            return Result.failure(ex)
        }
        catch (ex:NoSuchElementException){
            return Result.failure(ex)
        }catch (ex: ExposedSQLException){
            return Result.failure(ex)
        }
    }

    override suspend fun deleteTask(taskId: Int, login: String): Result<Unit> {
        val user = empRepository.getUserByLogin(login)?:return Result.failure(UserNotFoundException())
        return when (user.role) {
            "employee" -> {
                return Result.failure(AuthException())
            }

            "director" -> {
                return try {
                    try {
                        val reportId = empRepository.getReportByTaskId(taskId).id
                        if (reportId!=null){
                            empRepository.deleteReport(reportId)
                        }
                        Result.success(empRepository.deleteTask(taskId))
                    }catch (e:Exception){
                        Result.success(empRepository.deleteTask(taskId))
                    }
                } catch (ex: Exception) {
                    Result.failure(ex)
                }
            }
            else -> Result.failure(InvalidRoleException())
        }
    }

}