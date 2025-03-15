package model

interface EmployeeTaskRegRepository {
    suspend fun allTasks():List<Task>
    suspend fun addTask()

    suspend fun addUser(login:String, passwordHash:String)
}