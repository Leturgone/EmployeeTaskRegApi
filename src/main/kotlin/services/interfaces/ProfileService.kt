package services.interfaces

import domain.model.CompanyWorker
import domain.model.Employee
import domain.model.Report
import domain.model.Task

interface ProfileService {

    suspend fun getProfile(login:String): Result<CompanyWorker>

    suspend fun getMyEmployees(login: String):Result<List<Employee>>

    suspend fun getMyTasks(login: String):Result<List<Task>>

    suspend fun getMyReports(login: String):Result<List<Report>>

    suspend fun getMyTasksCount(login: String):Result<Int>

}