package domain.repository

import domain.model.Employee

interface EmployeeRepository {
    suspend fun getEmployeesByDirId(directorId:Int):List<Employee>

    suspend fun getEmployeeByName(name:String, directorId: Int):List<Employee>

    suspend fun getEmployeeById(employeeId:Int): Employee

    suspend fun getEmployeeByUserId(userId:Int): Employee

}