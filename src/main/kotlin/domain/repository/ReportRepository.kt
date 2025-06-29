package domain.repository

import domain.model.Report
import java.time.LocalDate

interface ReportRepository {
    suspend fun addReport(report: Report):Int

    suspend fun updateReportPath(path: String, reportDate: LocalDate, reportId: Int)

    suspend fun getReport(id:Int): Report

    suspend fun deleteReport(id:Int)

    suspend fun getReportByTaskId(taskId:Int): Report

    suspend fun getReportFilePath(id:Int):String?

    suspend fun resetMarkReport(reportId: Int)

    suspend fun markReport(mark:Boolean, reportId: Int)

    suspend fun getDirectorReports(directorId: Int):List<Report>

    suspend fun getEmployeeReports(employeeId: Int):List<Report>
}