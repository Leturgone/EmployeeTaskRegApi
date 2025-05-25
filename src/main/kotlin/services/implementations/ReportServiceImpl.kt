package services.implementations

import domain.model.Report
import domain.repository.EmployeeTaskRegRepository
import domain.repository.FileRepository
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.exceptions.ExposedSQLException
import services.*
import services.interfaces.ReportService
import java.time.LocalDate

class ReportServiceImpl(
    private val empRepository: EmployeeTaskRegRepository,
    private val fileRepository: FileRepository
    ) : ReportService {

    override suspend fun getReportById(reportId: Int): Result<Report> {
        return try {
            Result.success(empRepository.getReport(reportId))
        }catch (e:Exception){
            Result.failure(e)
        }

    }

    override suspend fun getReportByTaskId(taskId: Int): Result<Report> {
        return try {
            Result.success(empRepository.getReportByTaskId(taskId))
        }catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun downloadReport(reportId: Int): Result<ByteArray> {
        try {
            val path = empRepository.getReportFilePath(reportId)
                ?: return Result.failure(FilePathException())
            val byteArray = fileRepository.downloadFile(path)
                ?: return Result.failure(DownloadFileException())
            return Result.success(byteArray)
        }catch (ex:Exception){
            return  Result.failure(ex)
        }
    }

    override suspend fun markReport(login:String,reportId: Int, status: Boolean):Result<Unit> {
        val user = empRepository.getUserByLogin(login)?:return Result.failure(UserNotFoundException())
        return when (user.role) {
            "employee" -> {
                return Result.failure(AuthException())
            }

            "director" -> {
                return try {
                    Result.success(empRepository.markReport(status,reportId))
                } catch (ex: Exception) {
                    Result.failure(ex)
                }
            }
            else -> Result.failure(InvalidRoleException())
        }

    }

    override suspend fun addReport(multiPartData: MultiPartData,login: String): Result<Unit> {
        var report: Report? = null
        var fileBytes: ByteArray? = null
        var fileName = "unknownRepFile.pdf"
        val user = empRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        if(user.role!="employee"){ return Result.failure(AuthException()) }
        try {
            multiPartData.forEachPart { partData ->
                when(partData){
                    is PartData.FormItem ->{
                        if (partData.name =="reportJson"){
                            val jsonReport = partData.value
                            report  = try {
                                Json.decodeFromString<Report>(jsonReport)
                            }catch (e:Exception){
                                partData.dispose
                                throw InvalidTaskJsonException()
                            }
                        }
                        partData.dispose
                    }
                    is PartData.FileItem ->{
                        partData.originalFileName?.let { fileName = it}
                        val channel = partData.provider()
                        fileBytes = channel.readRemaining().readByteArray()
                        partData.dispose
                    }

                    else -> {partData.dispose}
                }
            }
        }catch (ex: InvalidTaskJsonException){ return Result.failure(ex) }

        if (report !=null && fileBytes!=null){
            try {
                val reportDate =  LocalDate.now()
                val repId = empRepository.addReport(report!!.copy(reportDate = reportDate))
                val path = fileRepository.uploadFile(user.id,user.role,
                    fileName,fileBytes!!)
                empRepository.updateReportPath(path!!,reportDate,repId)
                return Result.success(Unit)
            }

            catch (ex:NullPointerException){ return Result.failure(ex) }
            catch (ex:NoSuchElementException){ return Result.failure(ex) }
            catch (ex: ExposedSQLException){ return Result.failure(ex) }

        }else{
            return Result.failure(MissingFileException())
        }
    }

    override suspend fun updateReport(reportId: Int, multiPartData: MultiPartData, login: String): Result<Unit> {
        var fileBytes: ByteArray? = null
        var fileName = "unknownRepFile.pdf"
        val user = empRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        if(user.role!="employee"){ return Result.failure(AuthException()) }
        try {
            multiPartData.forEachPart { partData ->
                when(partData){
                    is PartData.FileItem ->{
                        partData.originalFileName?.let { fileName = it}
                        val channel = partData.provider()
                        fileBytes = channel.readRemaining().readByteArray()
                        partData.dispose
                    }
                    else -> {partData.dispose}
                }
            }
        }catch (ex: InvalidTaskJsonException){ return Result.failure(ex) }

        if (fileBytes!=null){
            try {
                val oldFilePath = empRepository.getReportFilePath(reportId)
                if(oldFilePath!=null){
                    fileRepository.deleteFile(oldFilePath)
                }
                val newFilePath  = fileRepository.uploadFile(user.id,user.role,
                    fileName,fileBytes!!)

                val reportDate = LocalDate.now()
                empRepository.updateReportPath(newFilePath!!,reportDate,reportId)
                empRepository.resetMarkReport(reportId)
                return Result.success(Unit)
            }
            catch (ex:NullPointerException){ return Result.failure(ex) }
            catch (ex:NoSuchElementException){ return Result.failure(ex) }
            catch (ex: ExposedSQLException){ return Result.failure(ex) }
        }else{
            return Result.failure(MissingFileException())
        }
    }
}