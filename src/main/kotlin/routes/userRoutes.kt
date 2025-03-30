package routes

import autharization.PasswordUtils
import autharization.Tokens
import data.dto.LoginRequest
import data.dto.LoginResponse
import data.dto.RegistrationRequest
import domain.repository.EmployeeTaskRegRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(repository: EmployeeTaskRegRepository){
    route("/users"){
        post("/register"){
            val request = call.receive<RegistrationRequest>()
            val hashedPassword = PasswordUtils.hashPassword(request.password)
            try {
                repository.addUser(request.login,hashedPassword,request.name,request.dirName)
                val token = Tokens.generateToken(request.login,request.password)
                call.respond(HttpStatusCode.OK, LoginResponse(token))
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: JsonConvertException) {
                call.respond(HttpStatusCode.BadRequest)
            }

        }
        get("/login"){
            val request = call.receive<LoginRequest>()

            val user = repository.getUserByLogin(request.login)
            if (user ==null){
                call.respond(HttpStatusCode.Unauthorized,"Неверный логин")
                return@get
            }
            if (!PasswordUtils.verifyPassword(request.password,user.passwordHash)){
                call.respond(HttpStatusCode.Unauthorized,"Неверный пароль")
                return@get
            }
            val token = Tokens.generateToken(user.login,user.role)
            call.respond(HttpStatusCode.OK, LoginResponse(token))

        }
    }
}