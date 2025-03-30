package services.implementations

import domain.model.Employee
import domain.repository.EmployeeTaskRegRepository
import services.interfaces.EmployeeService
import services.AuthException
import services.InvalidRoleException
import services.UserNotFoundException

class EmployeeServiceImpl(private val empRepository: EmployeeTaskRegRepository): EmployeeService {
    override suspend fun getEmployeeByName(login:String,empName:String): Result<List<Employee>> {
        val user = empRepository.getUserByLogin(login)?:return Result.failure(UserNotFoundException())

        return when(user.role){
            "employee" -> {
                Result.failure(AuthException())
            }
            "director" -> {
                try {
                    val dirId  = empRepository.getDirectorByUserId(user.id).id
                    val employeeList = empRepository.getEmployeeByName(empName,dirId)
                    Result.success(employeeList)
                }catch (ex:Exception){ Result.failure(ex) }
            }

            else -> {Result.failure(InvalidRoleException())}
        }
    }

    override suspend fun getEmployeeById(employeeId:Int): Result<Employee> {
        return try {
            val emp = empRepository.getEmployeeById(employeeId)
            Result.success(emp)
        }catch (ex:Exception){
            Result.failure(ex)
        }
    }
}