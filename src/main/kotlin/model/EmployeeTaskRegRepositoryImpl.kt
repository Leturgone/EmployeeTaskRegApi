package model

import db.TaskDAO
import db.daoToTaskModel
import db.suspendTransaction

class EmployeeTaskRegRepositoryImpl:EmployeeTaskRegRepository {
    override suspend fun allTasks(): List<Task> = suspendTransaction {
        TaskDAO.all().map(::daoToTaskModel)
    }
}