package servicies

import data.model.CompanyWorker
import data.repository.EmployeeTaskRegRepository

class ProfileServiceImpl(private val empRepository: EmployeeTaskRegRepository):ProfileService {
    override suspend fun getProfile(login:String ): Result<CompanyWorker> {
        val user = empRepository.getUserByLogin(login)

         return if (user != null) {
            when(user.role){
                "employee" -> {
                    Result.success(empRepository.getEmployeeByUserId(user.id))
                }
                "director" -> {
                    Result.success(empRepository.getDirectorByUserId(user.id))
                }
                else -> Result.failure(InvalidRoleException())

            }
        } else {
            Result.failure(UserNotFoundException())
        }
    }


}