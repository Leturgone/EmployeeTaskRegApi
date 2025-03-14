package db

import kotlinx.coroutines.Dispatchers
import model.Director
import model.Employee
import model.Report
import model.Task
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object DirectorTable : IntIdTable("director") {
    val name = varchar("name", 45)
    val login = varchar("login", 45).uniqueIndex()
    val password = varchar("password", 60)
}

class DirectorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DirectorDAO>(DirectorTable)

    var name by DirectorTable.name
    var login by DirectorTable.login
    var password by DirectorTable.password
}

object EmployeeTable : IntIdTable("employee") {
    val name = varchar("name", 45)
    val login = varchar("login", 45).uniqueIndex()
    val password = varchar("password", 60)
    val director = reference("director_id", DirectorTable, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE).nullable()
}

class EmployeeDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmployeeDAO>(EmployeeTable)
    var name by EmployeeTable.name
    var login by EmployeeTable.login
    var password by EmployeeTable.password
    var director by DirectorDAO optionalReferencedOn EmployeeTable.director
}
object TaskTable : IntIdTable("task") {
    val title = varchar("title", 45)
    val taskDesk = varchar("task_desk", 200)
    val documentName = varchar("document_name", 45).nullable()
    val documentPath = varchar("document_path", 45).nullable()
    val taskStartDate = date("task_start_date")
    val taskEndDate = date("task_end_date")
    val employee = reference("employee_id", EmployeeTable, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE).nullable()
    val director = reference("director_id", DirectorTable, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE).nullable()
}

class TaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TaskTable)

    var title by TaskTable.title
    var taskDesk by TaskTable.taskDesk
    var documentName by TaskTable.documentName
    var documentPath by TaskTable.documentPath
    var taskStartDate by TaskTable.taskStartDate
    var taskEndDate by TaskTable.taskEndDate
    var employee by EmployeeDAO optionalReferencedOn TaskTable.employee
    var director by DirectorDAO optionalReferencedOn TaskTable.director
}
object ReportTable : IntIdTable("report") {
    val reportDate = date("report_date")
    val documentName = varchar("document_name", 45).nullable()
    val documentPath = varchar("document_path", 45).nullable()
    val status = varchar("status", 45)
    val task = reference("task_id", TaskTable, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE) // CASCADE здесь, если удаление задачи должно удалять и отчеты
    val employee = reference("employee_id", EmployeeTable, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE).nullable()
    val director = reference("director_id", DirectorTable, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE).nullable()
}

class ReportDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReportDAO>(ReportTable)

    var reportDate by ReportTable.reportDate
    var documentName by ReportTable.documentName
    var documentPath by ReportTable.documentPath
    var status by ReportTable.status
    var task by TaskDAO referencedOn ReportTable.task // Используем TaskDAO
    var employee by EmployeeDAO optionalReferencedOn ReportTable.employee // Используем EmployeeDAO
    var director by DirectorDAO optionalReferencedOn ReportTable.director // Используем DirectorDAO
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToModel(dao: IntEntity): Any?{
    return when(dao){
        is TaskDAO -> Task(
            title = dao.title,
            taskDesc = dao.taskDesk,
            documentName = dao.documentName,
            startDate = dao.taskStartDate,
            endDate = dao.taskEndDate,
            employeeId = dao.employee!!.id.value,
            directorId = dao.director!!.id.value,
            documentPath = dao.documentPath
        )
        is DirectorDAO -> Director(
            name = dao.name,
            login = dao.login,
            password = dao.password
        )
        is EmployeeDAO -> Employee(
            name = dao.name,
            login = dao.login,
            password = dao.password,
            directorId = dao.director?.id?.value
        )
        is ReportDAO -> Report(
            reportDate = dao.reportDate,
            documentName = dao.documentName,
            status = dao.status,
            taskId = dao.task.id.value,
            employeeId = dao.employee?.id?.value,
            directorId = dao.director?.id?.value,
            documentPath = dao.documentPath
        )

        else -> {null}
    }
}
fun daoToTaskModel(dao:TaskDAO):Task = Task(
    title = dao.title,
    taskDesc = dao.taskDesk,
    documentName = dao.documentName,
    startDate = dao.taskStartDate,
    endDate = dao.taskEndDate,
    employeeId = dao.employee!!.id.value,
    directorId = dao.director!!.id.value,
    documentPath = dao.documentPath)
