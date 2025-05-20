package services.interfaces

import domain.model.Employee
import domain.model.Task

interface EmployeeService {

    suspend fun getEmployeeByName(login:String,empName:String):Result<List<Employee>>

    suspend fun getEmployeeById(employeeId:Int):Result<Employee>

    suspend fun getEmployeeTaskCountById(employeeId:Int):Result<Int>

    suspend fun getEmployeeCurrentTask(employeeId: Int):Result<Task>
}