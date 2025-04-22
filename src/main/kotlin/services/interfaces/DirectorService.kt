package services.interfaces

import domain.model.Director

interface DirectorService {
    suspend fun getDirectorById(directorId:Int):Result<Director>
}