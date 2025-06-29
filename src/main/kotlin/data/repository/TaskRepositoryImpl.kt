package data.repository

import db.*
import domain.model.Task
import domain.repository.TaskRepository
import org.jetbrains.exposed.sql.SortOrder

class TaskRepositoryImpl:TaskRepository {

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

    override suspend fun deleteTask(id: Int) = suspendTransaction {
        val task = TaskDAO[id]
        task.delete()
    }

    override suspend fun getDirectorTasks(directorId: Int): List<Task>  = suspendTransaction{
        TaskDAO.find { TaskTable.director eq directorId }.map(::daoToTaskModel)
    }

    override suspend fun getEmployeeTasks(employeeId: Int): List<Task> = suspendTransaction {
        TaskDAO.find { TaskTable.employee eq employeeId }.map(::daoToTaskModel)
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