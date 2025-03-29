package servicies

import data.model.CompanyWorker
import data.model.Employee
import data.repository.EmployeeTaskRegRepository
import java.lang.NumberFormatException

class ProfileServiceImpl(private val empRepository: EmployeeTaskRegRepository):ProfileService {
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


}