package servicies

import data.model.CompanyWorker
import data.model.Director
import data.model.Employee
import data.model.Task

interface ProfileService {

    suspend fun getProfile(login:String): Result<CompanyWorker>
    

    //suspend fun addTask(task:Task,)
}