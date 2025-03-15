package model

import db.AppUserDAO
import db.TaskDAO
import db.daoToTaskModel
import db.suspendTransaction

class EmployeeTaskRegRepositoryImpl:EmployeeTaskRegRepository {
    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO.all().map(::daoToTaskModel)
    }

    override suspend fun addTask() {
        TODO("Not yet implemented")
    }

    override suspend fun addUser(login: String, passwordHash: String): Unit = suspendTransaction{
        AppUserDAO.new {
            this.login = login
            this.passwordHash = passwordHash
            this.role = "employee"
        }
    }

}