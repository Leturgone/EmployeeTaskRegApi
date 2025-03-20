package model

interface EmployeeTaskRegRepository {
    suspend fun allTasks():List<Task>

    suspend fun addTask(task: Task)

    suspend fun getTask(id:Int):Task

    suspend fun addReport(report: Report)

    suspend fun getReport(id:Int):Report

    suspend fun getEmployeesByDirId(directorId:Int):List<Employee>

    suspend fun getEmployeeByName(name:String, directorId: Int):List<Employee>

    suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String)

    suspend fun getUserByLogin(login: String):AppUser?

    suspend fun getEmployeeById(employeeId:Int):Employee

    suspend fun getEmployeeByUserId(userId:Int):Employee

    suspend fun getDirectorByUserId(userId: Int):Director

}