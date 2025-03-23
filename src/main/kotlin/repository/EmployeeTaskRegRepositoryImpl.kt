package repository

import db.*
import model.*

class EmployeeTaskRegRepositoryImpl: EmployeeTaskRegRepository {
    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO.all().map(::daoToTaskModel)
    }

    override suspend fun updateTaskPath(path: String, taskId: Int) = suspendTransaction {
        TaskDAO[taskId].documentPath = path
    }

    override suspend fun addTask(task: Task): Int = suspendTransaction {
        TaskDAO.new {
            this.title = task.title
            this.taskDesk = task.taskDesc
            this.documentName = task.documentName
            this.taskStartDate  = task.startDate
            this.taskEndDate = task.endDate
            this.employee = EmployeeDAO.find { EmployeeTable.id eq task.employeeId }.firstOrNull()
            this.director = DirectorDAO.find { DirectorTable.id  eq task.directorId}.firstOrNull()
            this.documentPath = null
        }.id.value
    }

    override suspend fun getTask(id: Int): Task = suspendTransaction{
        daoToTaskModel(TaskDAO.find { TaskTable.id eq id }.first())
    }

    override suspend fun addReport(report: Report): Int = suspendTransaction{
        ReportDAO.new {
            this.reportDate = report.reportDate
            this.documentName = report.documentName
            this.status = report.status
            this.documentPath = null
            this.task = TaskDAO.find { TaskTable.id eq report.taskId }.first()
            this.employee = EmployeeDAO.find { EmployeeTable.id eq report.employeeId }.firstOrNull()
            this.director = DirectorDAO.find { DirectorTable.id eq report.directorId }.firstOrNull()

        }.id.value
    }

    override suspend fun updateReportPath(path: String, reportId: Int) = suspendTransaction {
        ReportDAO[reportId].documentPath = path
    }

    override suspend fun getReport(id: Int): Report = suspendTransaction{
        daoToReportModel(ReportDAO.find { ReportTable.id eq id }.first())
    }

    override suspend fun getEmployeesByDirId(directorId: Int): List<Employee>  = suspendTransaction {
        EmployeeDAO.find {EmployeeTable.director eq directorId}.map(::daoToEmployeeModel)
    }

    override suspend fun getEmployeeByName(name: String, directorId: Int): List<Employee>  = suspendTransaction{
        EmployeeDAO.find { EmployeeTable.director eq directorId }
            .filter { it.name.startsWith(name, ignoreCase = true) }.map(::daoToEmployeeModel)
    }


    override suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String): Unit = suspendTransaction{
        val dir = DirectorDAO.find { DirectorTable.name eq dirName}.firstOrNull()
        dir?.let {
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

    }

    override suspend fun getUserByLogin(login: String): AppUser? = suspendTransaction {
        AppUserDAO.find { AppUserTable.login eq  login }.firstOrNull()?.let {
            AppUser(it.id.value,it.login,it.passwordHash,it.role)
        }
    }

    override suspend fun getEmployeeById(employeeId: Int): Employee = suspendTransaction{
        daoToEmployeeModel(EmployeeDAO.find { EmployeeTable.id eq employeeId }.first())
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
        TaskDAO.find { TaskTable.director eq directorId }.count { it.status == "Решено" }
    }

    override suspend fun getEmployeeResolvedTasksCount(employeeId: Int): Int  = suspendTransaction{
        ReportDAO.find { TaskTable.employee eq employeeId }.count { it.status == "Решено" }
    }

}