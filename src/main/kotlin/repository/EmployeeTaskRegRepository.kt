package repository

import model.*

interface EmployeeTaskRegRepository {
    suspend fun allTasks():List<Task>

    suspend fun addTask(task: Task):Int

    suspend fun updateTaskPath(path:String,taskId:Int)

    suspend fun getTask(id:Int): Task

    suspend fun addReport(report: Report):Int

    suspend fun updateReportPath(path:String,reportId:Int)

    suspend fun getReport(id:Int): Report

    suspend fun markReport(mark:Boolean, reportId: Int)

    suspend fun getEmployeesByDirId(directorId:Int):List<Employee>

    suspend fun getEmployeeByName(name:String, directorId: Int):List<Employee>

    suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String)

    suspend fun getUserByLogin(login: String): AppUser?

    suspend fun getEmployeeById(employeeId:Int): Employee

    suspend fun getEmployeeByUserId(userId:Int): Employee

    suspend fun getDirectorByUserId(userId: Int): Director

    suspend fun getDirectorTasks(directorId: Int):List<Task>

    suspend fun getEmployeeTasks(employeeId: Int):List<Task>

    suspend fun getDirectorReports(directorId: Int):List<Report>

    suspend fun getEmployeeReports(employeeId: Int):List<Report>

    suspend fun getDirResolvedTasksCount(directorId: Int):Int

    suspend fun getEmployeeResolvedTasksCount(employeeId: Int):Int
}