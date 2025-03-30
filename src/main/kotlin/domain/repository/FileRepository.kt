package domain.repository

interface FileRepository {
    suspend fun uploadFile(userId:Int,category:String, fileName:String, fileBytes: ByteArray): String?

    suspend fun downloadFile(filePath:String):ByteArray?
}