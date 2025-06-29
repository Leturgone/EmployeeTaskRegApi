package domain.repository

import domain.model.Director

interface DirectorRepository{

    suspend fun getDirectorById(directorId:Int): Director

    suspend fun getDirectorByUserId(userId: Int): Director
}