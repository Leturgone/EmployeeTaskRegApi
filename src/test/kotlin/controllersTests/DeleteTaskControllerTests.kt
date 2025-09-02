package controllersTests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import services.AuthException
import services.UserNotFoundException
import services.interfaces.TaskService

class DeleteTaskControllerTests(private val taskService: TaskService) {
    suspend fun handle(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()

        val login = principal?.payload?.getClaim("login")?.asString()
        val taskId = call.parameters["taskId"]?.toIntOrNull()
        if (taskId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid task id")
            return
        }
        if (login != null) {
            taskService.deleteTask(taskId,login).onSuccess{
                call.respond(HttpStatusCode.OK)
            }.onFailure { e->
                when(e){
                    is UserNotFoundException -> call.respond(HttpStatusCode.InternalServerError, "User not found")
                    is AuthException -> call.respond(HttpStatusCode.Forbidden,"Only directors can delete tasks")
                    is Exception -> call.respond(HttpStatusCode.BadRequest, "Task not found")
                }
            }
        }else{
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}