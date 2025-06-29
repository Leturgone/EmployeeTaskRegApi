package data.repository

import db.*
import domain.model.Report
import domain.repository.ReportRepository
import java.time.LocalDate

class ReportRepositoryImpl:ReportRepository {
    override suspend fun addReport(report: Report): Int = suspendTransaction{
        ReportDAO.new {
            this.reportDate = report.reportDate!!
            this.documentName = report.documentName
            this.status = report.status
            this.documentPath = null
            this.task = TaskDAO.find { TaskTable.id eq report.taskId }.first()
            this.employee = EmployeeDAO.find { EmployeeTable.id eq report.employeeId }.firstOrNull()
            this.director = DirectorDAO.find { DirectorTable.id eq report.directorId }.firstOrNull()

        }.id.value
    }

    override suspend fun updateReportPath(path: String, reportDate:LocalDate, reportId: Int) = suspendTransaction {
        ReportDAO[reportId].documentPath = path
        ReportDAO[reportId].reportDate =reportDate
    }

    override suspend fun getReport(id: Int): Report = suspendTransaction{
        daoToReportModel(ReportDAO.find { ReportTable.id eq id }.first())
    }

    override suspend fun deleteReport(id: Int)  = suspendTransaction{
        val report = ReportDAO[id]
        val task = report.task
        task.status = "В процессе"
        report.delete()
    }

    override suspend fun getReportByTaskId(taskId: Int): Report  = suspendTransaction{
        daoToReportModel(ReportDAO.find { ReportTable.task eq taskId}.first())
    }

    override suspend fun getReportFilePath(id: Int): String?  = suspendTransaction{
        ReportDAO[id].documentPath
    }

    override suspend fun resetMarkReport(reportId: Int) = suspendTransaction {
        val report = ReportDAO[reportId]
        val task = report.task
        report.status = "Ожидание"
        task.status = "В процессе"
    }

    override suspend fun markReport(mark: Boolean, reportId: Int)  = suspendTransaction{
        val report = ReportDAO[reportId]
        val task = report.task
        when(mark){
            true -> {
                report.status = "Принято"
                task.status = "Завершена"
            }
            false -> {
                report.status = "На доработке"
                task.status = "В процессе"
            }
        }
    }

    override suspend fun getDirectorReports(directorId: Int): List<Report>  = suspendTransaction{
        ReportDAO.find { ReportTable.director eq directorId }.map(::daoToReportModel)
    }

    override suspend fun getEmployeeReports(employeeId: Int): List<Report>  = suspendTransaction{
        ReportDAO.find { ReportTable.employee eq employeeId }.map(::daoToReportModel)
    }
}