package servicies

import data.model.Report
import io.ktor.http.content.*

interface ReportService {

    suspend fun getReportById(reportId:Int):Result<Report>

    suspend fun downloadReport(reportId: Int): Result<ByteArray>

    suspend fun markReport(login:String,reportId: Int, status:Boolean):Result<Unit>

    suspend fun addReport(multiPartData: MultiPartData, login: String):Result<Unit>
}