package services.implementations

import domain.model.CompanyWorker
import domain.model.Employee
import domain.model.Report
import domain.model.Task
import domain.repository.*
import services.*
import services.interfaces.ProfileService

class ProfileServiceImpl(private val appUserRepository: AppUserRepository,
                         private val employeeRepository: EmployeeRepository,
                         private val taskRepository: TaskRepository,
                         private val reportRepository: ReportRepository,
                         private val directorRepository: DirectorRepository): ProfileService {
    override suspend fun getProfile(login:String ): Result<CompanyWorker> {
        val user = appUserRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())

         return when(user.role){
                 "employee" -> {
                     Result.success(employeeRepository.getEmployeeByUserId(user.id))
                 }
                 "director" -> {
                     Result.success(directorRepository.getDirectorByUserId(user.id))
                 }
                 else -> Result.failure(InvalidRoleException())
             }

    }

    override suspend fun getMyEmployees(login: String): Result<List<Employee>> {
        val user = appUserRepository.getUserByLogin(login)?:return Result.failure(UserNotFoundException())

        return when(user.role){
           "employee" -> { Result.failure(AuthException()) }
           "director" -> {
               try {
                   val dirId  = directorRepository.getDirectorByUserId(user.id).id
                   val empList = employeeRepository.getEmployeesByDirId(dirId)
                   Result.success(empList)
               }catch (ex:Exception){ Result.failure(NoSuchElementException()) }
           }

           else -> Result.failure(InvalidRoleException())
       }
    }

    override suspend fun getMyTasks(login: String): Result<List<Task>> {
        val user = appUserRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        return when(user.role){
            "employee" -> {
                try {
                    val empId  = employeeRepository.getEmployeeByUserId(user.id).id
                    val empTasks = taskRepository.getEmployeeTasks(empId)
                    Result.success(empTasks)
                }
                catch (ex:Exception){ Result.failure(EmployeeNotFoundException()) }
            }
            "director" -> {
                try {
                    val dirId  = directorRepository.getDirectorByUserId(user.id).id
                    println(dirId)
                    val dirTasks = taskRepository.getDirectorTasks(dirId)
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
        val user = appUserRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        return when(user.role){
            "employee" -> {
                try {
                    val empId  = employeeRepository.getEmployeeByUserId(user.id).id
                    val reportList = reportRepository.getEmployeeReports(empId)
                    Result.success(reportList)
                }catch (ex:Exception){ Result.failure(EmployeeNotFoundException()) }
            }
            "director" -> {
                try {
                    val dirId  = directorRepository.getDirectorByUserId(user.id).id
                    val reportList = reportRepository.getDirectorReports(dirId)
                    Result.success(reportList)
                }catch (ex:Exception){ Result.failure(DirectorNotFoundException()) }
            }

            else -> Result.failure(InvalidRoleException())
        }
    }

    override suspend fun getMyTasksCount(login: String): Result<Int> {
        val user = appUserRepository.getUserByLogin(login)?: return Result.failure(UserNotFoundException())
        return when(user.role){
            "employee" -> {
                try {
                    val empId  = employeeRepository.getEmployeeByUserId(user.id).id
                    val count = taskRepository.getEmployeeResolvedTasksCount(empId)
                    Result.success(count)
                }catch (ex:Exception){ Result.failure(EmployeeNotFoundException()) }
            }
            "director" -> {
                try {
                    val dirId  = directorRepository.getDirectorByUserId(user.id).id
                    val count = taskRepository.getDirResolvedTasksCount(dirId)
                    Result.success(count)
                }catch (ex:Exception){ Result.failure(DirectorNotFoundException()) }
            }

            else -> Result.failure(InvalidRoleException())
        }
    }


}