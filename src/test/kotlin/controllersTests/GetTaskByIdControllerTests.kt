package controllersTests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import services.interfaces.TaskService

class GetTaskByIdControllerTests(private val taskService: TaskService) {
    suspend fun handle(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val taskId = call.parameters["taskId"]?.toIntOrNull()
        if (taskId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid task id")
            return
        }
        if (login!=null){
            taskService.getTaskById(taskId).onSuccess { task->
                call.respond(HttpStatusCode.OK,task)
            }.onFailure {
                call.respond(HttpStatusCode.NotFound,"Task not found")
            }
        }
        else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}