package data.repository

import db.*
import domain.model.*
import domain.repository.EmployeeTaskRegRepository
import org.jetbrains.exposed.sql.SortOrder
import java.time.LocalDate

class EmployeeTaskRegRepositoryImpl: EmployeeTaskRegRepository {
    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO.all().map(::daoToTaskModel)
    }

    override suspend fun updateTaskPath(path: String, taskId: Int) = suspendTransaction {
        TaskDAO[taskId].documentPath = path
    }

    override suspend fun addTask(task: Task): Int = suspendTransaction {
        val empDao = EmployeeDAO.find { EmployeeTable.id eq task.employeeId }.firstOrNull()?:throw NoSuchElementException()
        TaskDAO.new {
            this.title = task.title
            this.taskDesk = task.taskDesc
            this.documentName = task.documentName
            this.taskStartDate  = task.startDate
            this.taskEndDate = task.endDate
            this.employee = empDao
            this.director = DirectorDAO.find { DirectorTable.id  eq task.directorId}.firstOrNull()
            this.documentPath = null
        }.id.value
    }

    override suspend fun getTask(id: Int): Task = suspendTransaction{
        daoToTaskModel(TaskDAO.find { TaskTable.id eq id }.first())
    }

    override suspend fun getTaskFilePath(id: Int): String?  = suspendTransaction{
        TaskDAO[id].documentPath
    }

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
        report.delete()
    }

    override suspend fun getReportByTaskId(taskId: Int): Report  = suspendTransaction{
        daoToReportModel(ReportDAO.find {ReportTable.task eq taskId}.first())
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

    override suspend fun getEmployeesByDirId(directorId: Int): List<Employee>  = suspendTransaction {
        EmployeeDAO.find {EmployeeTable.director eq directorId}.map(::daoToEmployeeModel)
    }

    override suspend fun getEmployeeByName(name: String, directorId: Int): List<Employee>  = suspendTransaction{
        EmployeeDAO.find { EmployeeTable.director eq directorId }
            .filter { it.name.startsWith(name, ignoreCase = true) }.map(::daoToEmployeeModel)
    }


    override suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String): Unit = suspendTransaction{
        val dir = DirectorDAO.find { DirectorTable.name eq dirName}.first()
        EmployeeDAO.new {
            this.user = AppUserDAO.new {
                this.login = login
                this.passwordHash = passwordHash
                this.role = "employee"
            }
            this.name = name
            this.director = dir
        }

    }

    override suspend fun getUserByLogin(login: String): AppUser? = suspendTransaction {
        AppUserDAO.find { AppUserTable.login eq  login }.firstOrNull()?.let {
            AppUser(it.id.value,it.login,it.passwordHash,it.role)
        }
    }

    override suspend fun getEmployeeById(employeeId: Int): Employee = suspendTransaction{
        daoToEmployeeModel(EmployeeDAO.find { EmployeeTable.id eq employeeId }.first())
    }

    override suspend fun getDirectorById(directorId: Int): Director  = suspendTransaction{
        daoToDirectorModel(DirectorDAO.find { DirectorTable.id eq directorId }.first())
    }

    override suspend fun getEmployeeByUserId(userId: Int): Employee = suspendTransaction {
        daoToEmployeeModel(EmployeeDAO.find { EmployeeTable.user eq userId }.first())
    }

    override suspend fun getDirectorByUserId(userId: Int): Director = suspendTransaction {
        daoToDirectorModel(DirectorDAO.find { DirectorTable.user eq userId }.first())
    }

    override suspend fun getDirectorTasks(directorId: Int): List<Task>  = suspendTransaction{
        TaskDAO.find { TaskTable.director eq directorId }.map(::daoToTaskModel)
    }

    override suspend fun getEmployeeTasks(employeeId: Int): List<Task> = suspendTransaction {
        TaskDAO.find { TaskTable.employee eq employeeId }.map(::daoToTaskModel)
    }

    override suspend fun getDirectorReports(directorId: Int): List<Report>  = suspendTransaction{
        ReportDAO.find { ReportTable.director eq directorId }.map(::daoToReportModel)
    }

    override suspend fun getEmployeeReports(employeeId: Int): List<Report>  = suspendTransaction{
        ReportDAO.find { ReportTable.employee eq employeeId }.map(::daoToReportModel)
    }

    override suspend fun getDirResolvedTasksCount(directorId: Int): Int  = suspendTransaction{
        TaskDAO.find { TaskTable.director eq directorId }.count { it.status == "Завершена" }
    }

    override suspend fun getEmployeeResolvedTasksCount(employeeId: Int): Int  = suspendTransaction{
        TaskDAO.find { TaskTable.employee eq employeeId }.count { it.status == "Завершена" }
    }

    override suspend fun getEmployeeCurrentTask(employeeId: Int): Task?  = suspendTransaction {
        TaskDAO.find { TaskTable.employee eq employeeId }.orderBy(TaskTable.taskStartDate to SortOrder.ASC)
            .toList().firstOrNull { it.status == "В процессе" }?.let {
                daoToTaskModel(it)
            }
    }

}