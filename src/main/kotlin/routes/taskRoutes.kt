package routes

import controllers.GetTaskByIdController
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(repository: EmployeeTaskRegRepository,
                     fileRepository: FileRepository,
                     getTaskByIdController: GetTaskByIdController){

    //Получение конкретного задания
    get("/getTask/{taskId}"){
        getTaskByIdController.handle(call)
    }

    get("/getTask/{taskId}/download"){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val taskId = call.parameters["taskId"]?.toInt()
        if (taskId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        if (login!=null){
            try {
                val path = repository.getTaskFilePath(taskId)
                if (path == null) {
                    call.respond(HttpStatusCode.InternalServerError, "File path not found for task $taskId")
                    return@get
                }
                val byteArray = fileRepository.downloadFile(path)
                if (byteArray == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to download file for task $taskId")
                    return@get
                }
                call.respond(HttpStatusCode.OK,byteArray)
            }catch (ex:Exception){
                call.respond(HttpStatusCode.NotFound,"Task $taskId not found")
            }
        }
        else {
            call.respond(HttpStatusCode.BadRequest, "Invalid token")
        }

    }
}