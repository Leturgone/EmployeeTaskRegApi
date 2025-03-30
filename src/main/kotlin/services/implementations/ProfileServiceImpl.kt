package services.implementations

import domain.model.CompanyWorker
import domain.model.Employee
import domain.model.Report
import domain.model.Task
import domain.repository.EmployeeTaskRegRepository
import services.*
import services.interfaces.ProfileService

class ProfileServiceImpl(private val empRepository: EmployeeTaskRegRepository): ProfileService {
    override suspend fun getProfile(login:String ): Result<CompanyWorker> {
        val user = empRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())

         return when(user.role){
                 "employee" -> {
                     Result.success(empRepository.getEmployeeByUserId(user.id))
                 }
                 "director" -> {
                     Result.success(empRepository.getDirectorByUserId(user.id))
                 }
                 else -> Result.failure(InvalidRoleException())
             }

    }

    override suspend fun getMyEmployees(login: String): Result<List<Employee>> {
        val user = empRepository.getUserByLogin(login)?:return Result.failure(UserNotFoundException())

        return when(user.role){
           "employee" -> { Result.failure(AuthException()) }
           "director" -> {
               try {
                   val dirId  = empRepository.getDirectorByUserId(user.id).id
                   val empList = empRepository.getEmployeesByDirId(dirId)
                   Result.success(empList)
               }catch (ex:Exception){ Result.failure(NoSuchElementException()) }
           }

           else -> Result.failure(InvalidRoleException())
       }
    }

    override suspend fun getMyTasks(login: String): Result<List<Task>> {
        val user = empRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        return when(user.role){
            "employee" -> {
                try {
                    val empId  = empRepository.getEmployeeByUserId(user.id).id
                    val empTasks = empRepository.getEmployeeTasks(empId)
                    Result.success(empTasks)
                }
                catch (ex:Exception){ Result.failure(EmployeeNotFoundException()) }
            }
            "director" -> {
                try {
                    val dirId  = empRepository.getDirectorByUserId(user.id).id
                    println(dirId)
                    val dirTasks = empRepository.getDirectorTasks(dirId)
                    Result.success(dirTasks)
                }
                catch (ex:Exception){
                    println(ex)
                    Result.failure(DirectorNotFoundException()) }
            }
            else -> Result.failure(InvalidRoleException())
        }
    }

    override suspend fun getMyReports(login: String): Result<List<Report>> {
        val user = empRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        return when(user.role){
            "employee" -> {
                try {
                    val empId  = empRepository.getEmployeeByUserId(user.id).id
                    val reportList = empRepository.getEmployeeReports(empId)
                    Result.success(reportList)
                }catch (ex:Exception){ Result.failure(EmployeeNotFoundException()) }
            }
            "director" -> {
                try {
                    val dirId  = empRepository.getDirectorByUserId(user.id).id
                    val reportList = empRepository.getDirectorReports(dirId)
                    Result.success(reportList)
                }catch (ex:Exception){ Result.failure(DirectorNotFoundException()) }
            }

            else -> Result.failure(InvalidRoleException())
        }
    }

    override suspend fun getMyTasksCount(login: String): Result<Int> {
        val user = empRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        return when(user.role){
            "employee" -> {
                try {
                    val empId  = empRepository.getEmployeeByUserId(user.id).id
                    val count = empRepository.getEmployeeResolvedTasksCount(empId)
                    Result.success(count)
                }catch (ex:Exception){ Result.failure(EmployeeNotFoundException()) }
            }
            "director" -> {
                try {
                    val dirId  = empRepository.getDirectorByUserId(user.id).id
                    val count = empRepository.getDirResolvedTasksCount(dirId)
                    Result.success(count)
                }catch (ex:Exception){ Result.failure(DirectorNotFoundException()) }
            }

            else -> Result.failure(InvalidRoleException())
        }
    }


}