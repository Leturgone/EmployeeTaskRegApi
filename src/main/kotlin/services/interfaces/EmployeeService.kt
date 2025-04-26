package services.interfaces

import domain.model.Employee

interface EmployeeService {

    suspend fun getEmployeeByName(login:String,empName:String):Result<List<Employee>>

    suspend fun getEmployeeById(employeeId:Int):Result<Employee>

    suspend fun getEmployeeTaskCountById(employeeId:Int):Result<Int>
}