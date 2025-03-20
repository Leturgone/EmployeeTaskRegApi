import autharization.PasswordUtils
import autharization.Tokens
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.EmployeeTaskRegRepository
import model.Report
import model.Requests.LoginRequest
import model.Requests.LoginResponse
import model.Requests.RegistrationRequest
import model.Task
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Application.configureSerialization(repository: EmployeeTaskRegRepository) {
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
                    val request = call.receive<Task>()
                    val login = principal?.payload?.getClaim("login")?.asString()

                    if (login != null) {
                        val user = repository.getUserByLogin(login)
                        if (user != null) {
                            if(user.role=="director") {
                                try {
                                    repository.addTask(request)
                                    call.respond(HttpStatusCode.OK)
                                }catch (ex:ExposedSQLException){
                                    call.respond(HttpStatusCode.BadRequest,"Tasks must be unique")
                                }catch (ex:NoSuchElementException){
                                    call.respond(HttpStatusCode.BadRequest,"Employee not found")
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
                    val request = call.receive<Report>()
                    val login = principal?.payload?.getClaim("login")?.asString()

                    if (login != null) {
                        val user = repository.getUserByLogin(login)
                        if (user != null) {
                            if(user.role=="employee") {
                                try {
                                    repository.addReport(request)
                                    call.respond(HttpStatusCode.OK)
                                }catch (ex:NoSuchElementException){
                                    call.respond(HttpStatusCode.BadRequest,"No task for report found")
                                }catch (ex: ExposedSQLException){
                                    call.respond(HttpStatusCode.BadRequest,"Reports must be unique")
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
                if (reportId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                if (login!=null){
                    try {
                        call.respond(HttpStatusCode.OK,repository.getReport(reportId))
                    }catch (ex:Exception){
                        call.respond(HttpStatusCode.NotFound,"Report not found")
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