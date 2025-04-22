package services.implementations

import domain.model.Director
import domain.repository.EmployeeTaskRegRepository
import services.interfaces.DirectorService

class DirectorServiceImpl(private val empRepository: EmployeeTaskRegRepository):DirectorService {
    override suspend fun getDirectorById(directorId: Int): Result<Director> {
        return try {
            val emp = empRepository.getDirectorById(directorId)
            Result.success(emp)
        }catch (ex:Exception){
            Result.failure(ex)
        }
    }

}