package model

import db.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

class EmployeeTaskRegRepositoryImpl:EmployeeTaskRegRepository {
    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO.all().map(::daoToTaskModel)
    }

    override suspend fun addTask(task: Task): Unit = suspendTransaction {
        TaskDAO.new {
            this.title = task.title
            this.taskDesk = task.taskDesc
            this.documentName = task.documentName
            this.taskStartDate  = task.startDate
            this.taskEndDate = task.endDate
            this.employee = EmployeeDAO.find { EmployeeTable.id eq task.employeeId }.firstOrNull()
            this.director = DirectorDAO.find { DirectorTable.id  eq task.directorId}.firstOrNull()
            this.documentPath = task.documentPath

        }
    }

    override suspend fun getTask(id: Int): Task  = suspendTransaction{
        daoToTaskModel(TaskDAO.find { TaskTable.id eq id }.first())
    }

    override suspend fun addReport(report: Report): Unit = suspendTransaction{
        ReportDAO.new {
            this.reportDate = report.reportDate
            this.documentName = report.documentName
            this.status = report.status
            this.documentPath = report.documentPath
            this.task = TaskDAO.find { TaskTable.id eq report.taskId }.first()
            this.employee = EmployeeDAO.find { EmployeeTable.id eq report.employeeId }.firstOrNull()
            this.director = DirectorDAO.find { DirectorTable.id eq report.directorId }.firstOrNull()

        }
    }

    override suspend fun getReport(id: Int): Report  = suspendTransaction{
        daoToReportModel(ReportDAO.find { ReportTable.id eq id }.first())
    }

    override suspend fun getEmployeesByDirId(directorId: Int): List<Employee>  = suspendTransaction {
        EmployeeDAO.find {EmployeeTable.director eq directorId}.map(::daoToEmployeeModel)
    }

    override suspend fun searchEmployeeByName(name: String,directorId: Int): List<Employee>  = suspendTransaction{
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

    override suspend fun findUserByLogin(login: String):AppUser? = suspendTransaction {
        AppUserDAO.find { AppUserTable.login eq  login }.firstOrNull()?.let {
            AppUser(it.id.value,it.login,it.passwordHash,it.role)
        }
    }

    override suspend fun findEmployeeById(employeeId: Int): Employee  = suspendTransaction{
        daoToEmployeeModel(EmployeeDAO.find { EmployeeTable.id eq employeeId }.first())
    }

    override suspend fun findEmployeeByUserId(userId: Int): Employee  = suspendTransaction {
        daoToEmployeeModel(EmployeeDAO.find { EmployeeTable.user eq userId }.first())
    }

    override suspend fun findDirectorByUserId(userId: Int): Director = suspendTransaction {
        daoToDirectorModel(DirectorDAO.find { DirectorTable.user eq userId }.first())
    }
}