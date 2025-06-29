package domain.repository

import domain.model.Task

interface TaskRepository {
    suspend fun allTasks():List<Task>

    suspend fun addTask(task: Task):Int

    suspend fun updateTaskPath(path:String,taskId:Int)

    suspend fun getTask(id:Int): Task

    suspend fun getTaskFilePath(id:Int):String?

    suspend fun deleteTask(id:Int)

    suspend fun getDirectorTasks(directorId: Int):List<Task>

    suspend fun getEmployeeTasks(employeeId: Int):List<Task>

    suspend fun getDirResolvedTasksCount(directorId: Int):Int

    suspend fun getEmployeeResolvedTasksCount(employeeId: Int):Int

    suspend fun getEmployeeCurrentTask(employeeId: Int):Task?
}