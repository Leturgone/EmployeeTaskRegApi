package servicies

import data.model.Employee
import data.repository.EmployeeTaskRegRepository

class EmployeeServiceImpl(private val empRepository: EmployeeTaskRegRepository):EmployeeService {
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
}