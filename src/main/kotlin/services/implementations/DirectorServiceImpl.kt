package services.implementations

import domain.model.Director
import domain.repository.DirectorRepository
import services.interfaces.DirectorService

class DirectorServiceImpl(private val dirRepository: DirectorRepository):DirectorService {
    override suspend fun getDirectorById(directorId: Int): Result<Director> {
        return try {
            val emp = dirRepository.getDirectorById(directorId)
            Result.success(emp)
        }catch (ex:Exception){
            Result.failure(ex)
        }
    }

}