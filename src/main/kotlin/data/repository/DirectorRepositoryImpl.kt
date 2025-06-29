package data.repository

import db.DirectorDAO
import db.DirectorTable
import db.daoToDirectorModel
import db.suspendTransaction
import domain.model.Director
import domain.repository.DirectorRepository

class DirectorRepositoryImpl:DirectorRepository {

    override suspend fun getDirectorById(directorId: Int): Director  = suspendTransaction{
        daoToDirectorModel(DirectorDAO.find { DirectorTable.id eq directorId }.first())
    }

    override suspend fun getDirectorByUserId(userId: Int): Director = suspendTransaction {
        daoToDirectorModel(DirectorDAO.find { DirectorTable.user eq userId }.first())
    }
}