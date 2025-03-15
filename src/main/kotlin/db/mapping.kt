package db

import kotlinx.coroutines.Dispatchers
import model.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object AppUserTable : IntIdTable("appUser") {
    val login = varchar("login", 45).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role",45)
}

class AppUserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AppUserDAO>(AppUserTable)
    var login by AppUserTable.login
    var passwordHash by AppUserTable.passwordHash
    var role by AppUserTable.role
}

object DirectorTable : IntIdTable("director") {
    val name = varchar("name", 45)
    val user = reference("user_id", AppUserTable, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

class DirectorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DirectorDAO>(DirectorTable)
    var name by DirectorTable.name
    var user by AppUserDAO referencedOn DirectorTable.user
}

object EmployeeTable : IntIdTable("employee") {
    val name = varchar("name", 45)
    val user = reference("user_id", AppUserTable, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val director = reference("director_id", DirectorTable, onDelete = ReferenceOption.SET_NULL, onUpdate = ReferenceOption.CASCADE).nullable()
}

class EmployeeDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmployeeDAO>(EmployeeTable)
    var name by EmployeeTable.name
    var user by AppUserDAO referencedOn DirectorTable.user
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


fun daoToTaskModel(dao:TaskDAO):Task = Task(
    title = dao.title,
    taskDesc = dao.taskDesk,
    documentName = dao.documentName,
    startDate = dao.taskStartDate,
    endDate = dao.taskEndDate,
    employeeId = dao.employee!!.id.value,
    directorId = dao.director!!.id.value,
    documentPath = dao.documentPath)

fun daoToUserModel(dao:AppUserDAO): AppUser = AppUser(
    login = dao.login,
    passwordHash = dao.passwordHash,
    role = dao.role
)

fun daoToDirectorModel(dao: DirectorDAO) = Director(
    name = dao.name,
    userId = dao.user.id.value
)

fun daoToEmployeeModel(dao: EmployeeDAO) = Employee(
    name = dao.name,
    userId = dao.user.id.value,
    directorId = dao.director!!.id.value
)

fun daoToReportModel(dao: ReportDAO) = Report(
    reportDate = dao.reportDate,
    documentName = dao.documentName,
    status = dao.status,
    taskId = dao.task.id.value,
    employeeId = dao.employee?.id?.value,
    directorId = dao.director?.id?.value,
    documentPath = dao.documentPath
)