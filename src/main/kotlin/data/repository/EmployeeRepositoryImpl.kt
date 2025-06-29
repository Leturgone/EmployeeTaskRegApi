package data.repository

import db.EmployeeDAO
import db.EmployeeTable
import db.daoToEmployeeModel
import db.suspendTransaction
import domain.model.Employee
import domain.repository.EmployeeRepository

class EmployeeRepositoryImpl:EmployeeRepository {

    override suspend fun getEmployeesByDirId(directorId: Int): List<Employee>  = suspendTransaction {
        EmployeeDAO.find {EmployeeTable.director eq directorId}.map(::daoToEmployeeModel)
    }

    override suspend fun getEmployeeByName(name: String, directorId: Int): List<Employee>  = suspendTransaction{
        EmployeeDAO.find { EmployeeTable.director eq directorId }
            .filter { it.name.startsWith(name, ignoreCase = true) }.map(::daoToEmployeeModel)
    }

    override suspend fun getEmployeeById(employeeId: Int): Employee = suspendTransaction{
        daoToEmployeeModel(EmployeeDAO.find { EmployeeTable.id eq employeeId }.first())
    }

    override suspend fun getEmployeeByUserId(userId: Int): Employee = suspendTransaction {
        daoToEmployeeModel(EmployeeDAO.find { EmployeeTable.user eq userId }.first())
    }
}