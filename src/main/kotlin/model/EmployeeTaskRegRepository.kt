package model

interface EmployeeTaskRegRepository {
    suspend fun allTasks():List<Task>

    suspend fun addTask()

    suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String)

    suspend fun findUserByLogin(login: String):AppUser?

    suspend fun findEmployeeByUserId(userId:Int):Employee

    suspend fun findDirectorByUserId(userId: Int):Director

}