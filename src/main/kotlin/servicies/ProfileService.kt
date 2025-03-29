package servicies

import data.model.CompanyWorker
import data.model.Employee

interface ProfileService {

    suspend fun getProfile(login:String): Result<CompanyWorker>

    suspend fun getMyEmployees(login: String):Result<List<Employee>>
}