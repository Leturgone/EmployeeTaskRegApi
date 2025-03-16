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
import model.Requests.LoginRequest
import model.Requests.LoginResponse
import model.Requests.RegistrationRequest

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

                val user = repository.findUserByLogin(request.login)
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
            authenticate("auth-jwt") {
                get("/profile") {
                    val principal = call.principal<JWTPrincipal>()
                    val login = principal?.payload?.getClaim("login")?.asString()

                    if (login != null) {
                        val user = repository.findUserByLogin(login)

                        if (user != null) {
                            when(user.role){
                                "employee" -> {
                                    call.respond(
                                        repository.findEmployeeByUserId(user.id)
                                    )
                                }
                                "director" -> {call.respond(repository.findDirectorByUserId(user.id))}
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

    }
}