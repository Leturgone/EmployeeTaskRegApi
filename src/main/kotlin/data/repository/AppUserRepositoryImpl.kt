package data.repository

import db.*
import domain.model.AppUser
import domain.repository.AppUserRepository

class AppUserRepositoryImpl:AppUserRepository {

    override suspend fun addUser(login: String,passwordHash: String, name:String, dirName:String): Unit = suspendTransaction{
        val dir = DirectorDAO.find { DirectorTable.name eq dirName}.first()
        EmployeeDAO.new {
            this.user = AppUserDAO.new {
                this.login = login
                this.passwordHash = passwordHash
                this.role = "employee"
            }
            this.name = name
            this.director = dir
        }

    }

    override suspend fun getUserByLogin(login: String): AppUser? = suspendTransaction {
        AppUserDAO.find { AppUserTable.login eq  login }.firstOrNull()?.let {
            AppUser(it.id.value,it.login,it.passwordHash,it.role)
        }
    }
}