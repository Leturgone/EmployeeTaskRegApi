package servicies

import data.model.Report

interface ReportService {

    suspend fun getReportById(reportId:Int):Result<Report>

    suspend fun downloadReport(reportId: Int): Result<ByteArray>

    suspend fun markReport(reportId: Int, status:Boolean)

}