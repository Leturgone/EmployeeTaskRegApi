package model

import db.*

class EmployeeTaskRegRepositoryImpl:EmployeeTaskRegRepository {
    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO.all().map(::daoToTaskModel)
    }

    override suspend fun addTask() {
        TODO("Not yet implemented")
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