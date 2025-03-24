package data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.io.files.FileNotFoundException
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createDirectories

class FileRepositoryImpl(private val fileDir:String): FileRepository {
    private val logger = LoggerFactory.getLogger(FileRepositoryImpl::class.java)

    override suspend fun uploadFile(userId: Int, category: String, fileName: String, fileBytes: ByteArray): String?  {
        return withContext(Dispatchers.IO) {
            val timeSnap = DateTimeFormatter.ofPattern("yyyMMddHHmmss").format(LocalDateTime.now())
            val userDir = "$fileDir\\$category\\user$userId\\"
            val newFileName = "$timeSnap$fileName"
            val filePath = Paths.get(userDir, newFileName).toString()
            try {
                if(!File(userDir).exists()){
                    Paths.get(userDir).createDirectories()
                    logger.info("Created directory $userDir by user with id $userId")
                }
                File(filePath).writeBytes(fileBytes)
                logger.info("Created file $filePath by user with id $userId")
                filePath
            } catch (e: Exception) {
                println("Error saving file: ${e.message}")
                null
            }
        }

    }

    override suspend fun downloadFile(filePath: String) : ByteArray? {
        return withContext(Dispatchers.IO) {
            val file = File(filePath)
            try {
                file.readBytes()
            } catch (e: FileNotFoundException) {
                println("File not found: $filePath")
                null
            } catch (e: IOException) {
                println("Error reading file: ${e.message}")
                null
            }
        }
    }
}