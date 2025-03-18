package model

interface EmployeeTaskRegRepository {
    suspend fun allTasks():List<Task>

    suspend fun addTask(task: Task)

    suspend fun getTask(id:Int):Task

    suspend fun addReport(report: Report)

    suspend fun getReport(id:Int):Report

    suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String)

    suspend fun findUserByLogin(login: String):AppUser?

    suspend fun findEmployeeById(employeeId:Int):Employee

    suspend fun findEmployeeByUserId(userId:Int):Employee

    suspend fun findDirectorByUserId(userId: Int):Director

}