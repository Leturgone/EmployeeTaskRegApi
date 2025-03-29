package servicies

import data.model.Employee

interface EmployeeService {

    suspend fun getEmployeeByName(login:String,empName:String):Result<List<Employee>>

    suspend fun getEmployeeById(employeeId:Int):Result<Employee>
}