package domain.repository

import domain.model.AppUser

interface AppUserRepository {

    suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String)

    suspend fun getUserByLogin(login: String): AppUser?
}