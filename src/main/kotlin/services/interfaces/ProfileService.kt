package services.interfaces

import data.model.CompanyWorker
import data.model.Employee
import data.model.Report
import data.model.Task

interface ProfileService {

    suspend fun getProfile(login:String): Result<CompanyWorker>

    suspend fun getMyEmployees(login: String):Result<List<Employee>>

    suspend fun getMyTasks(login: String):Result<List<Task>>

    suspend fun getMyReports(login: String):Result<List<Report>>

    suspend fun getMyTasksCount(login: String):Result<Int>

}