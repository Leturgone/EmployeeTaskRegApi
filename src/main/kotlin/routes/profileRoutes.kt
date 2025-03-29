package routes

import controllers.AddTaskController
import controllers.GetProfileController
import data.model.Report
import data.model.Task
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Route.profileRoutes(repository:EmployeeTaskRegRepository, fileRepository: FileRepository,
                        getProfileController: GetProfileController,
                        addTaskController: AddTaskController){
    route("/profile"){

        //Получение профиля
        get { getProfileController.handle(call) }

        //Добавление задания
        post("/addTask"){ addTaskController.handle(call) }

        //Добавление отчета
        post("/addReport"){
            val principal = call.principal<JWTPrincipal>()
            val multipartData = call.receiveMultipart()
            var report: Report? = null
            var fileBytes: ByteArray? = null
            var fileName = "unknownRepFile.pdf"
            val login = principal?.payload?.getClaim("login")?.asString()

            if (login != null) {
                val user = repository.getUserByLogin(login)
                if (user != null) {
                    if(user.role=="employee") {
                        multipartData.forEachPart { partData ->
                            when(partData){
                                is PartData.FormItem ->{
                                    if (partData.name =="reportJson"){
                                        val jsonReport = partData.value
                                        report  = try {
                                            Json.decodeFromString<Report>(jsonReport)
                                        }catch (e:Exception){
                                            call.respond(HttpStatusCode.BadRequest, "Invalid Report JSON $e")
                                            partData.dispose
                                            return@forEachPart
                                        }
                                    }
                                    partData.dispose
                                }
                                is PartData.FileItem ->{
                                    partData.originalFileName?.let { fileName = it}
                                    val channel = partData.provider()
                                    fileBytes = channel.readRemaining().readByteArray()
                                    partData.dispose
                                }

                                else -> {partData.dispose}
                            }
                        }

                        if (report !=null && fileBytes!=null){
                            try {
                                val repId = repository.addReport(report!!)
                                val path = fileRepository.uploadFile(user.id,user.role,
                                    fileName,fileBytes!!)
                                repository.updateReportPath(path!!,repId)
                                call.respond(HttpStatusCode.OK)
                            }catch (ex:NullPointerException){
                                call.respond(HttpStatusCode.InternalServerError,"Error saving file")
                            }
                            catch (ex:NoSuchElementException){
                                call.respond(HttpStatusCode.BadRequest,"No task for report found")
                            }catch (ex: ExposedSQLException){
                                call.respond(HttpStatusCode.BadRequest,"Reports must be unique")
                            }
                        }else{
                            call.respond(HttpStatusCode.BadRequest,"Missing report file")
                        }

                    }else{
                        call.respond(HttpStatusCode.Forbidden,"Only employee can create reports")
                    }

                }else{
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }
        }

        //Получение списка сотрудников
        get("/myEmployees"){
            val principal = call.principal<JWTPrincipal>()
            val login = principal?.payload?.getClaim("login")?.asString()
            if (login != null) {
                val user = repository.getUserByLogin(login)

                if (user != null) {
                    when(user.role){
                        "employee" -> {
                            call.respond(HttpStatusCode.Forbidden,"Only director have employees")
                        }
                        "director" -> {
                            try {
                                val dirId  = repository.getDirectorByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getEmployeesByDirId(dirId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Director not found")
                            }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
            }
        }

        //Поиск сотрудника по имени
        get("/myEmployees/{empName}"){
            val principal = call.principal<JWTPrincipal>()
            val login = principal?.payload?.getClaim("login")?.asString()
            val empName = call.parameters["empName"].toString()
            if (login != null) {
                val user = repository.getUserByLogin(login)

                if (user != null) {
                    when(user.role){
                        "employee" -> {
                            call.respond(HttpStatusCode.Forbidden,"Only directors can search employees")
                        }
                        "director" -> {
                            try {
                                val dirId  = repository.getDirectorByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getEmployeeByName(empName,dirId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Director not found")
                            }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
            }
        }
        get("/myEmployees/{employeeId}"){
            val principal = call.principal<JWTPrincipal>()
            val login = principal?.payload?.getClaim("login")?.asString()
            val employeeId = call.parameters["employeeId"]?.toInt()
            if (employeeId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            if (login!=null){
                try {
                    call.respond(HttpStatusCode.OK,repository.getEmployeeById(employeeId))
                }catch (ex:Exception){
                    call.respond(HttpStatusCode.NotFound,"Employee not found")
                }
            }
            else {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
            }
        }

        //Получение списка задач
        get("/myTasks"){
            val principal = call.principal<JWTPrincipal>()
            val login = principal?.payload?.getClaim("login")?.asString()
            if (login != null) {
                val user = repository.getUserByLogin(login)
                if (user != null) {
                    when(user.role){
                        "employee" -> {
                            try {
                                val empId  = repository.getEmployeeByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getEmployeeTasks(empId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Employee not found")
                            }
                        }
                        "director" -> {
                            try {
                                val dirId  = repository.getDirectorByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getDirectorTasks(dirId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Director not found")
                            }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
            }
        }

        //Получение списка отчетов
        get("/myReports"){
            val principal = call.principal<JWTPrincipal>()
            val login = principal?.payload?.getClaim("login")?.asString()
            if (login != null) {
                val user = repository.getUserByLogin(login)
                if (user != null) {
                    when(user.role){
                        "employee" -> {
                            try {
                                val empId  = repository.getEmployeeByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getEmployeeReports(empId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Employee not found")
                            }
                        }
                        "director" -> {
                            try {
                                val dirId  = repository.getDirectorByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getDirectorReports(dirId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Director not found")
                            }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
            }
        }
        get("/myTaskCount"){
            val principal = call.principal<JWTPrincipal>()
            val login = principal?.payload?.getClaim("login")?.asString()
            if (login != null) {
                val user = repository.getUserByLogin(login)
                if (user != null) {
                    when(user.role){
                        "employee" -> {
                            try {
                                val empId  = repository.getEmployeeByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getEmployeeResolvedTasksCount(empId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Employee not found")
                            }
                        }
                        "director" -> {
                            try {
                                val dirId  = repository.getDirectorByUserId(user.id).id
                                call.respond(HttpStatusCode.OK,repository.getDirResolvedTasksCount(dirId))
                            }catch (ex:Exception){
                                call.respond(HttpStatusCode.NotFound,"Director not found")
                            }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
            }
        }

    }
}