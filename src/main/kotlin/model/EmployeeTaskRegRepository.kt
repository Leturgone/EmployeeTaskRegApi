package model

interface EmployeeTaskRegRepository {
    suspend fun allTasks():List<Task>
}