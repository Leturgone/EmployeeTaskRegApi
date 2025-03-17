package model

import db.*
import java.time.LocalDate

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

    override suspend fun findEmployeeByUserId(userId: Int): Employee  = suspendTransaction {
        EmployeeDAO.find { EmployeeTable.user eq userId }.first().let {
            Employee(it.id.value,it.name,it.user.id.value,it.director?.id?.value)
        }
    }

    override suspend fun findDirectorByUserId(userId: Int): Director = suspendTransaction {
        DirectorDAO.find { DirectorTable.user eq userId }.first().let {
            Director(it.id.value,it.name,it.user.id.value)
        }
    }
}