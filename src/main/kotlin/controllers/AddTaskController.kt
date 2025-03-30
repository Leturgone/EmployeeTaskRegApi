package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import services.AuthException
import services.InvalidTaskJsonException
import services.UserNotFoundException
import services.interfaces.TaskService

class AddTaskController(private val taskService: TaskService) {
    suspend fun handle(call:ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val multipartData = call.receiveMultipart()

        val login = principal?.payload?.getClaim("login")?.asString()
        if (login != null) {
            taskService.addTask(multipartData,login).onSuccess {
                call.respond(HttpStatusCode.OK)
            }.onFailure { e ->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.NotFound, "User not found")
                    is AuthException -> call.respond(HttpStatusCode.Forbidden,"Only directors can create tasks")
                    is InvalidTaskJsonException -> call.respond(HttpStatusCode.BadRequest, "Invalid Task JSON")
                    is NullPointerException -> call.respond(HttpStatusCode.InternalServerError,"Error saving file")
                    is NoSuchElementException -> call.respond(HttpStatusCode.BadRequest,"No employee for task found")
                    is ExposedSQLException -> call.respond(HttpStatusCode.Conflict,"Tasks must be unique")
                    is Exception -> call.respond(HttpStatusCode.InternalServerError,"Task provide error")
                }
            }
        }else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}