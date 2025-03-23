import autharization.PasswordUtils
import autharization.Tokens
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import model.Report
import model.Requests.LoginRequest
import model.Requests.LoginResponse
import model.Requests.RegistrationRequest
import model.Task
import org.jetbrains.exposed.exceptions.ExposedSQLException
import repository.EmployeeTaskRegRepository
import repository.FileRepository

fun Application.configureSerialization(repository: EmployeeTaskRegRepository, fileRepository: FileRepository) {
    install(ContentNegotiation) {
        json()

    }
    routing {
        route("/tasks") {
            get {
                val tasks = repository.allTasks()
                call.respond(tasks)
            }
        }

        //Регистрация и логин
        route("/users"){
            post("/register"){
                val request = call.receive<RegistrationRequest>()
                val hashedPassword = PasswordUtils.hashPassword(request.password)
                try {
                    repository.addUser(request.login,hashedPassword,request.name,request.dirName)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("/login"){
                val request = call.receive<LoginRequest>()

                val user = repository.getUserByLogin(request.login)
                if (user ==null){
                    call.respond(HttpStatusCode.Unauthorized,"Неверный логин")
                    return@post
                }
                if (!PasswordUtils.verifyPassword(request.password,user.passwordHash)){
                    call.respond(HttpStatusCode.Unauthorized,"Неверный пароль")
                    return@post
                }
                val token = Tokens.generateToken(user.login,user.role)
                call.respond(HttpStatusCode.OK,LoginResponse(token))

            }
        }
        authenticate("auth-jwt") {
            //Получение профиля сотрудника
            route("/profile"){
                get {
                    val principal = call.principal<JWTPrincipal>()
                    val login = principal?.payload?.getClaim("login")?.asString()

                    if (login != null) {
                        val user = repository.getUserByLogin(login)

                        if (user != null) {
                            when(user.role){
                                "employee" -> {
                                    call.respond(
                                        repository.getEmployeeByUserId(user.id)
                                    )
                                }
                                "director" -> {call.respond(repository.getDirectorByUserId(user.id))}
                            }
                        } else {
                            call.respond(HttpStatusCode.NotFound, "User not found")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid token")
                    }
                }
                //Добавление задания
                post("/addTask"){
                    val principal = call.principal<JWTPrincipal>()
                    val multipartData = call.receiveMultipart()
                    var task: Task? = null
                    var fileBytes: ByteArray? = null
                    var fileName = "unknownTaskFile.pdf"
                    val login = principal?.payload?.getClaim("login")?.asString()
                    if (login != null) {
                        val user = repository.getUserByLogin(login)
                        if (user != null) {
                            if(user.role=="director") {
                                multipartData.forEachPart { partData ->
                                    when(partData){
                                        is PartData.FormItem ->{
                                            if (partData.name =="taskJson"){
                                                val jsonTask = partData.value
                                                task = try{
                                                    Json.decodeFromString<Task>(jsonTask)
                                                }catch (e:Exception){
                                                    call.respond(HttpStatusCode.BadRequest, "Invalid Task JSON $e")
                                                    partData.dispose
                                                    return@forEachPart
                                                }
                                            }
                                            partData.dispose
                                        }
                                        is PartData.FileItem ->{
                                            partData.originalFileName?.let { fileName = it }
                                            val channel = partData.provider()
                                            fileBytes = channel.readRemaining().readByteArray()
                                            partData.dispose
                                        }

                                        else -> {partData.dispose}
                                    }
                                }
                                if(task!=null){
                                    try {
                                        if (fileBytes!=null){
                                            val taskId = repository.addTask(task!!)
                                            val path = fileRepository.uploadFile(user.id,user.role,
                                            fileName,fileBytes!!)
                                            repository.updateTaskPath(path!!,taskId)
                                            call.respond(HttpStatusCode.OK)
                                        }else{
                                            repository.addTask(task!!)
                                            call.respond(HttpStatusCode.OK)
                                        }
                                    }catch (ex:NullPointerException){
                                        call.respond(HttpStatusCode.InternalServerError,"Error saving file")
                                    }
                                    catch (ex:NoSuchElementException){
                                        call.respond(HttpStatusCode.BadRequest,"No employee for task found")
                                    }catch (ex: ExposedSQLException){
                                        call.respond(HttpStatusCode.BadRequest,"Tasks must be unique")
                                    }
                                }

                            }else{
                                call.respond(HttpStatusCode.Forbidden,"Only directors can create tasks")
                            }

                        }else{
                            call.respond(HttpStatusCode.NotFound, "User not found")
                        }
                    }
                }
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

            //Получение конкретного задания
            get("/getTask/{taskId}"){
                val principal = call.principal<JWTPrincipal>()
                val login = principal?.payload?.getClaim("login")?.asString()
                val taskId = call.parameters["taskId"]?.toInt()
                if (taskId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                if (login!=null){
                    try {
                        call.respond(HttpStatusCode.OK,repository.getTask(taskId))
                    }catch (ex:Exception){
                        call.respond(HttpStatusCode.NotFound,"Task not found")
                    }
                }
                else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid token")
                }
            }
            //Получение конкретного отчета
            get("/getReport/{reportId}"){
                val principal = call.principal<JWTPrincipal>()
                val login = principal?.payload?.getClaim("login")?.asString()
                val reportId = call.parameters["reportId"]?.toInt()
                var report: Report? = null
                if (reportId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                if (login!=null){
                    try {
                        report = repository.getReport(reportId)
                        call.respond(HttpStatusCode.OK,report!!)
                    }catch (ex:Exception){
                        call.respond(HttpStatusCode.NotFound,"Report not found")
                    }
                }
                else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid token")
                }
            }

            get("/getReport/{reportId}/download"){
                val principal = call.principal<JWTPrincipal>()
                val login = principal?.payload?.getClaim("login")?.asString()
                val reportId = call.parameters["reportId"]?.toInt()
                if (reportId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                if (login!=null){
                    try {
                        val path = repository.getReportFilePath(reportId)
                        if (path == null) {
                            call.respond(HttpStatusCode.InternalServerError, "File path not found for report $reportId")
                            return@get
                        }
                        val byteArray = fileRepository.downloadFile(path)
                        if (byteArray == null) {
                            call.respond(HttpStatusCode.InternalServerError, "Failed to download file for report $reportId")
                            return@get
                        }
                        call.respond(HttpStatusCode.OK,byteArray)
                    }catch (ex:Exception){
                        call.respond(HttpStatusCode.NotFound,"Report not found")
                    }
                }
                else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid token")
                }

            }

            patch("/markReport/{reportId}/{status}"){
                val principal = call.principal<JWTPrincipal>()
                val login = principal?.payload?.getClaim("login")?.asString()
                val reportId = call.parameters["reportId"]?.toInt()
                val status = call.parameters["status"]?.toBooleanStrictOrNull()
                if (reportId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@patch
                }
                if (status == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid status")
                    return@patch
                }
                if (login!=null) {
                    val user = repository.getUserByLogin(login)
                    if (user != null) {
                        when (user.role) {
                            "employee" -> {
                                call.respond(HttpStatusCode.Forbidden, "Only director can mark report")
                            }

                            "director" -> {
                                try {
                                    call.respond(HttpStatusCode.OK, repository.markReport(status, reportId))
                                } catch (ex: Exception) {
                                    call.respond(HttpStatusCode.NotFound, "Report not found")
                                }
                            }
                        }
                    }
                }
                else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid token")
                }



            }
            //Получение конкретного сотрудника
            get("/employee/{employeeId}"){
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
        }

    }
}