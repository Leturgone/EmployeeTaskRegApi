package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import servicies.DownloadFileException
import servicies.FilePathException
import servicies.TaskService

class DownloadTaskController(private val taskService: TaskService) {
    suspend fun handle(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val taskId = call.parameters["taskId"]?.toIntOrNull()
        if (taskId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid task id")
            return
        }
        if (login!=null){
            taskService.downloadTask(taskId).onSuccess { task ->
                call.respond(HttpStatusCode.OK,task)
            }.onFailure { e ->
                when(e){
                    is FilePathException -> call.respond(HttpStatusCode.InternalServerError, "File path not found for task $taskId")
                    is DownloadFileException -> call.respond(HttpStatusCode.InternalServerError, "Failed to download file for task $taskId")
                    is Exception -> call.respond(HttpStatusCode.NotFound,"Task $taskId not found")
                }
            }
        }
        else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }
    }
}