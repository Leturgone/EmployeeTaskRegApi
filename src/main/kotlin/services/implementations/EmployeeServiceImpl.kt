package services.implementations

import domain.model.Employee
import domain.model.Task
import domain.repository.*
import services.interfaces.EmployeeService
import services.AuthException
import services.InvalidRoleException
import services.UserNotFoundException

class EmployeeServiceImpl(private val appUserRepository: AppUserRepository,
                          private val directorRepository: DirectorRepository,
                          private val taskRepository: TaskRepository,
                          private val employeeRepository: EmployeeRepository): EmployeeService {
    override suspend fun getEmployeeByName(login:String,empName:String): Result<List<Employee>> {
        val user = appUserRepository.getUserByLogin(login)?:return Result.failure(UserNotFoundException())

        return when(user.role){
            "employee" -> {
                Result.failure(AuthException())
            }
            "director" -> {
                try {
                    val dirId  = directorRepository.getDirectorByUserId(user.id).id
                    val employeeList = employeeRepository.getEmployeeByName(empName,dirId)
                    Result.success(employeeList)
                }catch (ex:Exception){ Result.failure(ex) }
            }

            else -> {Result.failure(InvalidRoleException())}
        }
    }

    override suspend fun getEmployeeById(employeeId:Int): Result<Employee> {
        return try {
            val emp = employeeRepository.getEmployeeById(employeeId)
            Result.success(emp)
        }catch (ex:Exception){
            Result.failure(ex)
        }
    }

    override suspend fun getEmployeeTaskCountById(employeeId: Int): Result<Int> {
        return try{
            val count = taskRepository.getEmployeeResolvedTasksCount(employeeId)
            Result.success(count)
        }catch (ex:Exception){
            Result.failure(ex)
        }
    }

    override suspend fun getEmployeeCurrentTask(employeeId: Int): Result<Task> {
        return try {
            val task = taskRepository.getEmployeeCurrentTask(employeeId)
            if (task!=null){
                Result.success(task)
            }else{
                throw Exception()
            }
        }catch (ex:Exception){
            Result.failure(ex)
        }
    }
}